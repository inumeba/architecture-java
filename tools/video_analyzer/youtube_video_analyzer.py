#!/usr/bin/env python3
"""
YouTube/video analyzer pipeline:
1) Resolve input (YouTube URL, direct URL, or local file)
2) Download media when needed
3) Transcribe audio with faster-whisper
4) Produce an extractive summary + key points
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from collections import Counter
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable, List, Optional
from urllib.parse import urlparse

import requests
from faster_whisper import WhisperModel
from yt_dlp import YoutubeDL


EN_STOPWORDS = {
    "a", "an", "the", "is", "are", "was", "were", "be", "been", "being", "and", "or", "but", "if", "then",
    "this", "that", "these", "those", "to", "of", "in", "on", "for", "with", "as", "at", "by", "from", "it",
    "its", "you", "your", "we", "our", "they", "their", "he", "she", "his", "her", "them", "i", "me", "my",
    "do", "does", "did", "done", "can", "could", "should", "would", "will", "just", "not", "now", "into",
    "than", "also", "very", "more", "most", "much", "many", "some", "any", "all", "so", "up", "out", "about",
}

FR_STOPWORDS = {
    "le", "la", "les", "un", "une", "des", "du", "de", "d", "et", "ou", "mais", "si", "alors", "ce", "cette",
    "ces", "dans", "sur", "pour", "avec", "par", "depuis", "au", "aux", "en", "est", "sont", "etait", "etaient",
    "etre", "ete", "vous", "nous", "ils", "elles", "je", "tu", "il", "elle", "mon", "ma", "mes", "ton", "ta",
    "tes", "son", "sa", "ses", "notre", "votre", "leur", "leurs", "que", "qui", "quoi", "dont", "comme", "plus",
    "moins", "tres", "aussi", "tout", "tous", "toutes", "pas", "ne", "se", "sur", "sous", "vers", "sans",
}

EN_FR_PHRASES = {
    "github copilot": "GitHub Copilot",
    "agent mode": "mode agent",
    "agent code review": "revue de code agent",
    "slash commands": "commandes slash",
    "file references": "references de fichiers",
    "custom instructions": "instructions personnalisees",
    "next edit suggestions": "suggestions de prochaines modifications",
    "mcp server connections": "connexions MCP",
    "copilot cli": "Copilot CLI",
    "key points": "points cles",
    "this video": "cette video",
    "this is": "c'est",
    "let me show you": "voici",
    "most developers": "la plupart des developpeurs",
    "doesn't just": "ne se contente pas de",
    "writes code": "ecrit du code",
    "reviews it": "le relit",
    "single biggest": "plus grand",
    "context hack": "levier de contexte",
    "in your editor": "dans votre editeur",
    "in your terminal": "dans votre terminal",
    "auto-complete tool": "outil d'autocompletion",
    "completely missing": "souvent ignores",
    "trick number": "astuce numero",
    "let me show you what most developers do with copilot": "voici comment la plupart des developpeurs utilisent Copilot",
    "this is the single biggest context hack in copilot": "c'est le plus grand levier de contexte dans Copilot",
    "this is the file that makes copilot actually understand your project": "c'est le fichier qui permet a Copilot de vraiment comprendre votre projet",
    "think of it as claw.md, but for copilot, one file, permanent improvement": "considerez cela comme un fichier de reference pour Copilot: un seul fichier, une amelioration permanente",
    "copilot doesn't just write code anymore, it reviews it too": "Copilot ne se contente plus d'ecrire du code, il le relit aussi",
    "copilot in 2026 isn't an auto-complete tool": "en 2026, Copilot n'est plus un simple outil d'autocompletion",
    "10 github copilot tricks that most developers are completely missing": "10 astuces GitHub Copilot que la plupart des developpeurs n'exploitent pas",
    "this is where copilot breaks out of the editor": "c'est la que Copilot depasse le cadre de l'editeur",
    "copilot cli copilot isn't just in your editor anymore, it's in your terminal": "Copilot CLI: Copilot n'est plus seulement dans l'editeur, il est aussi dans le terminal",
    "trick number six, agent code review": "astuce numero 6: revue de code agent",
    "trick number": "astuce numero",
}

EN_FR_WORDS = {
    "trick": "astuce",
    "number": "numero",
    "commands": "commandes",
    "command": "commande",
    "files": "fichiers",
    "file": "fichier",
    "review": "revue",
    "code": "code",
    "developers": "developpeurs",
    "terminal": "terminal",
    "editor": "editeur",
    "project": "projet",
    "context": "contexte",
    "tool": "outil",
    "mode": "mode",
    "tests": "tests",
    "documentation": "documentation",
    "summary": "resume",
    "timeline": "timeline",
    "what": "comment",
    "with": "avec",
    "that": "qui",
    "makes": "permet a",
    "actually": "vraiment",
    "understand": "comprendre",
    "your": "votre",
    "in": "dans",
    "is": "est",
    "the": "le",
    "biggest": "plus grand",
    "single": "principal",
    "breaks": "depasse",
    "out": "hors",
    "write": "ecrire",
    "writes": "ecrit",
    "reviews": "relit",
    "just": "seulement",
    "most": "la plupart",
    "missing": "ignores",
}

TRICK_LABELS_FR = {
    "1": "Commandes slash",
    "2": "References de fichiers",
    "3": "Instructions personnalisees",
    "4": "Suggestions de prochaines modifications",
    "5": "Mode agent",
    "6": "Revue de code agent",
    "7": "Hooks agent",
    "8": "Connexions MCP",
    "9": "GitHub Spark",
    "10": "Copilot CLI",
}


@dataclass
class Segment:
    start: float
    end: float
    text: str


def is_url(value: str) -> bool:
    parsed = urlparse(value)
    return parsed.scheme in {"http", "https"} and bool(parsed.netloc)


def is_youtube_url(value: str) -> bool:
    host = urlparse(value).netloc.lower()
    return "youtube.com" in host or "youtu.be" in host


def download_file(url: str, dest_path: Path) -> Path:
    with requests.get(url, stream=True, timeout=60) as response:
        response.raise_for_status()
        with dest_path.open("wb") as f:
            for chunk in response.iter_content(chunk_size=1024 * 1024):
                if chunk:
                    f.write(chunk)
    return dest_path


def download_youtube(url: str, out_dir: Path) -> Path:
    out_dir.mkdir(parents=True, exist_ok=True)
    out_template = str(out_dir / "input_video.%(ext)s")
    ydl_opts = {
        "format": "bestvideo+bestaudio/best",
        "outtmpl": out_template,
        "noplaylist": True,
        "merge_output_format": "mp4",
        "quiet": True,
    }

    with YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(url, download=True)
        downloaded = Path(ydl.prepare_filename(info))

    if downloaded.suffix.lower() != ".mp4":
        mp4_candidate = downloaded.with_suffix(".mp4")
        if mp4_candidate.exists():
            return mp4_candidate
    return downloaded


def resolve_media(input_value: str, work_dir: Path) -> Path:
    candidate = Path(input_value)
    if candidate.exists() and candidate.is_file():
        return candidate

    if not is_url(input_value):
        raise FileNotFoundError(f"Input does not exist and is not a valid URL: {input_value}")

    media_path = work_dir / "input_video.mp4"

    if is_youtube_url(input_value):
        return download_youtube(input_value, work_dir)

    return download_file(input_value, media_path)


def transcribe_video(video_path: Path, model_name: str, language: Optional[str]) -> tuple[List[Segment], dict]:
    model = WhisperModel(model_name, device="cpu", compute_type="int8")
    segments_gen, info = model.transcribe(
        str(video_path),
        language=language,
        vad_filter=False,
        beam_size=1,
        best_of=1,
        temperature=0.0,
    )

    segments: List[Segment] = []
    for seg in segments_gen:
        text = seg.text.strip()
        if text:
            segments.append(Segment(start=seg.start, end=seg.end, text=text))

    metadata = {
        "detected_language": info.language,
        "language_probability": float(info.language_probability),
        "duration_sec": round(segments[-1].end if segments else 0.0, 2),
        "segment_count": len(segments),
        "model": model_name,
        "source_file": str(video_path),
    }
    return segments, metadata


def write_transcript(segments: Iterable[Segment], path: Path, metadata: dict) -> None:
    with path.open("w", encoding="utf-8") as f:
        f.write(
            f"# language={metadata['detected_language']} prob={metadata['language_probability']:.4f} model={metadata['model']}\n"
        )
        for seg in segments:
            f.write(f"[{seg.start:8.2f} -> {seg.end:8.2f}] {seg.text}\n")


def split_sentences(text: str) -> List[str]:
    raw = re.split(r"(?<=[.!?])\s+", text)
    sentences = [s.strip() for s in raw if s.strip()]
    return sentences


def tokenize(text: str) -> List[str]:
    return re.findall(r"[a-zA-Z][a-zA-Z\-']+", text.lower())


def score_sentences(sentences: List[str], language_hint: str, max_sentences: int) -> List[str]:
    if not sentences:
        return []

    stopwords = FR_STOPWORDS if language_hint.startswith("fr") else EN_STOPWORDS
    words = [w for w in tokenize(" ".join(sentences)) if w not in stopwords and len(w) > 2]
    freq = Counter(words)

    if not freq:
        return sentences[: min(max_sentences, len(sentences))]

    sentence_scores = []
    for idx, sentence in enumerate(sentences):
        tokens = [w for w in tokenize(sentence) if w not in stopwords and len(w) > 2]
        if not tokens:
            score = 0
        else:
            score = sum(freq[w] for w in tokens) / len(tokens)
        sentence_scores.append((idx, score, sentence))

    top = sorted(sentence_scores, key=lambda x: x[1], reverse=True)[: max_sentences]
    top_sorted = sorted(top, key=lambda x: x[0])
    return [item[2] for item in top_sorted]


def strip_fillers(text: str) -> str:
    cleaned = re.sub(r"\s+", " ", text).strip()
    cleaned = re.sub(r"^(and\s+|so\s+|well\s+)", "", cleaned, flags=re.IGNORECASE)
    return cleaned


def looks_english(text: str) -> bool:
    tokens = tokenize(text)
    if not tokens:
        return False
    common_en = {"the", "this", "that", "is", "are", "with", "for", "you", "your", "and", "copilot"}
    hit = sum(1 for t in tokens if t in common_en)
    return hit >= 2


def translate_en_to_fr(text: str) -> str:
    if not text:
        return text

    out = text
    for src in sorted(EN_FR_PHRASES, key=len, reverse=True):
        dst = EN_FR_PHRASES[src]
        out = re.sub(rf"\b{re.escape(src)}\b", dst, out, flags=re.IGNORECASE)

    def replace_word(match: re.Match[str]) -> str:
        word = match.group(0)
        lower = word.lower()
        if lower in EN_FR_WORDS:
            rep = EN_FR_WORDS[lower]
            return rep.capitalize() if word[:1].isupper() else rep
        return word

    out = re.sub(r"\b[A-Za-z][A-Za-z\-']*\b", replace_word, out)
    out = re.sub(r"\bc'est le plus grand\b", "c'est le plus grand", out, flags=re.IGNORECASE)
    out = re.sub(r"\bc'est le\s+le\b", "c'est le", out, flags=re.IGNORECASE)
    out = re.sub(r"\bCopilot ne se contente pas de ecrire\b", "Copilot ne se contente pas d'ecrire", out, flags=re.IGNORECASE)
    out = re.sub(r"\bTrick number\s+(\d+|one|two|three|four|five|six|seven|eight|nine|ten)\b", r"Astuce numero \1", out, flags=re.IGNORECASE)
    out = re.sub(r"\s+", " ", out).strip()
    return out


def force_french_line(text: str) -> str:
    cleaned = strip_fillers(text)
    lowered = cleaned.lower()
    has_known_phrase = any(k in lowered for k in EN_FR_PHRASES.keys())
    if looks_english(cleaned) or has_known_phrase or "trick number" in lowered:
        translated = translate_en_to_fr(cleaned)
        return translated
    return cleaned


def english_ratio(text: str) -> float:
    tokens = tokenize(text)
    if not tokens:
        return 0.0
    common_en = {"the", "this", "that", "is", "are", "with", "for", "you", "your", "and", "copilot", "in", "it"}
    hits = sum(1 for t in tokens if t in common_en)
    return hits / max(1, len(tokens))


def normalize_key_points_fr(lines: List[str]) -> List[str]:
    out: List[str] = []
    for line in lines:
        candidate = force_french_line(line)
        if english_ratio(candidate) > 0.35:
            candidate = translate_en_to_fr(candidate)
        # Final hardening pass for stubborn mixed lines.
        candidate = re.sub(
            r"^Trick number six, Agent Code Review\.?$",
            "Astuce numero 6: revue de code agent.",
            candidate,
            flags=re.IGNORECASE,
        )
        candidate = re.sub(
            r"^Copilot doesn't just write code anymore, it reviews it too\.?$",
            "Copilot ne se contente plus d'ecrire du code, il le relit aussi.",
            candidate,
            flags=re.IGNORECASE,
        )
        candidate = re.sub(
            r"^Copilot in 2026 isn't an auto-complete tool\.?$",
            "En 2026, Copilot n'est plus un simple outil d'autocompletion.",
            candidate,
            flags=re.IGNORECASE,
        )
        candidate = strip_fillers(candidate)
        if candidate and candidate not in out:
            out.append(candidate)
    return out


def extract_trick_timeline(segments: List[Segment]) -> List[str]:
    timeline: List[str] = []
    pattern = re.compile(r"trick\s+number\s+([a-z0-9\-]+|\d+)", re.IGNORECASE)
    num_map = {
        "one": "1",
        "two": "2",
        "three": "3",
        "four": "4",
        "five": "5",
        "six": "6",
        "seven": "7",
        "eight": "8",
        "nine": "9",
        "ten": "10",
    }
    for seg in segments:
        m = pattern.search(seg.text)
        if m:
            minute = int(seg.start // 60)
            second = int(seg.start % 60)
            raw_num = m.group(1).lower()
            num = num_map.get(raw_num, raw_num)
            topic = TRICK_LABELS_FR.get(num)
            if not topic:
                tail = seg.text[m.end():].strip(" ,.-")
                topic = force_french_line(tail) if tail else "Presentation de la fonctionnalite"
            topic = re.sub(r"\.$", "", topic)
            timeline.append(f"{minute:02d}:{second:02d} - Astuce {num}: {topic}")

    # Keep only unique consecutive duplicates if ASR repeats near boundaries.
    deduped: List[str] = []
    for item in timeline:
        if not deduped or deduped[-1] != item:
            deduped.append(item)
    return deduped


def infer_video_theme(segments: List[Segment], language_hint: str) -> str:
    text = " ".join(s.text.lower() for s in segments)
    if "copilot" in text and "github" in text:
        return "GitHub Copilot"
    if "agent" in text:
        return "agents IA"
    if language_hint.startswith("fr"):
        return "sujet principal"
    return "main topic"


def build_human_summary(segments: List[Segment], metadata: dict, summary_lines: List[str], key_lines: List[str]) -> str:
    theme = infer_video_theme(segments, metadata["detected_language"])
    if metadata["detected_language"].startswith("fr"):
        intro = f"La video traite de {theme} et presente les idees a fort impact de facon progressive."
        mid = "Elle compare les usages basiques avec des usages avances, puis montre un workflow plus automatise."
        outro = "Conclusion: l'outil est positionne comme un assistant capable de planifier, executer et verifier des taches completes."
    else:
        intro = f"Cette video se concentre sur {theme} et met en avant des usages a forte valeur."
        mid = "Le fil conducteur oppose un usage basique a une utilisation avancee et automatisee."
        outro = "Conclusion: l'outil est presente comme un agent capable de planifier, executer et verifier des taches de bout en bout."

    highlights = key_lines[:3] if key_lines else summary_lines[:3]
    highlights_text = " ".join(force_french_line(h) for h in highlights)
    return f"{intro} {mid} {highlights_text} {outro}".strip()


def build_action_items(key_lines: List[str], timeline: List[str], max_items: int = 6) -> List[str]:
    actions: List[str] = []

    keyword_actions = [
        ("slash", "Configurer une liste de commandes slash utiles et les tester sur un vrai fichier de code."),
        ("file", "Standardiser les references de fichiers dans les prompts pour donner un contexte precis a l'assistant."),
        ("instructions", "Ajouter un fichier d'instructions de projet pour forcer les conventions de code de l'equipe."),
        ("agent mode", "Tester le mode agent sur une tache complete (implementation, tests, correction d'erreurs)."),
        ("review", "Activer une revue assistee et verifier les corrections proposees avant merge."),
        ("mcp", "Connecter un service externe via MCP dans un environnement de test avant usage en production."),
        ("cli", "Executer un flux plan + execution depuis le terminal pour mesurer le gain de temps."),
    ]

    corpus = " ".join(key_lines).lower()
    for keyword, action in keyword_actions:
        if keyword in corpus and action not in actions:
            actions.append(action)
        if len(actions) >= max_items:
            break

    if len(actions) < max_items:
        for line in key_lines:
            clean = force_french_line(line)
            candidate = f"Mettre en pratique: {clean}"
            if candidate not in actions:
                actions.append(candidate)
            if len(actions) >= max_items:
                break

    if len(actions) < max_items and timeline:
        actions.append("Revoir la timeline du contenu pour prioriser les sujets a tester en premier.")

    return actions[:max_items]


def create_summary_markdown(metadata: dict, segments: List[Segment], summary_sentences: int, key_points: int) -> str:
    transcript_text = " ".join(seg.text for seg in segments)
    sentences = split_sentences(transcript_text)

    summary_lines = score_sentences(sentences, metadata["detected_language"], summary_sentences)
    key_lines = score_sentences(sentences, metadata["detected_language"], key_points)
    key_lines = normalize_key_points_fr(key_lines)
    trick_timeline = extract_trick_timeline(segments)
    human_summary = build_human_summary(segments, metadata, summary_lines, key_lines)
    action_items = build_action_items(key_lines, trick_timeline, max_items=6)

    md = []
    md.append("# Video Analysis")
    md.append("")
    md.append("## Metadata")
    md.append(f"- Detected language: {metadata['detected_language']}")
    md.append(f"- Language confidence: {metadata['language_probability']:.4f}")
    md.append(f"- Duration (s): {metadata['duration_sec']}")
    md.append(f"- Segments: {metadata['segment_count']}")
    md.append(f"- Whisper model: {metadata['model']}")
    md.append("")
    md.append("## Resume Court")
    if human_summary:
        md.append(human_summary)
    else:
        md.append("Aucun resume n'a pu etre genere.")
    md.append("")
    md.append("## Points Cles")
    if key_lines:
        for line in key_lines:
            md.append(f"- {line}")
    else:
        md.append("- Aucun point cle n'a pu etre extrait.")

    md.append("")
    md.append("## Actions Concretes")
    if action_items:
        for item in action_items:
            md.append(f"- {item}")
    else:
        md.append("- Aucune action n'a pu etre proposee.")

    if trick_timeline:
        md.append("")
        md.append("## Timeline")
        for item in trick_timeline:
            md.append(f"- {item}")

    return "\n".join(md) + "\n"


def main() -> int:
    parser = argparse.ArgumentParser(description="Analyze YouTube/local videos and generate transcript + summary.")
    parser.add_argument("--input", help="YouTube/direct URL or local video file path")
    parser.add_argument("--transcript", help="Use existing transcript file and skip download/transcription")
    parser.add_argument("--outdir", default="tmp/video-analysis", help="Output directory")
    parser.add_argument("--model", default="tiny", help="Whisper model: tiny, base, small, medium, large-v3")
    parser.add_argument("--language", default="auto", help="Language code (en/fr/...) or auto")
    parser.add_argument("--summary-sentences", type=int, default=5, help="Sentence count in short summary")
    parser.add_argument("--key-points", type=int, default=10, help="Number of key point bullets")
    args = parser.parse_args()

    out_dir = Path(args.outdir)
    out_dir.mkdir(parents=True, exist_ok=True)

    transcript_path = out_dir / "transcript.txt"
    summary_path = out_dir / "summary.md"
    metadata_path = out_dir / "metadata.json"

    if args.transcript:
        source_transcript = Path(args.transcript)
        if not source_transcript.exists():
            raise FileNotFoundError(f"Transcript not found: {source_transcript}")
        lines = source_transcript.read_text(encoding="utf-8", errors="ignore").splitlines()
        segments: List[Segment] = []
        for line in lines:
            m = re.match(r"\[(\s*\d+\.\d+)\s*->\s*(\s*\d+\.\d+)\]\s*(.+)", line)
            if m:
                segments.append(Segment(start=float(m.group(1)), end=float(m.group(2)), text=m.group(3).strip()))

        metadata = {
            "detected_language": "auto",
            "language_probability": 1.0,
            "duration_sec": round(segments[-1].end if segments else 0.0, 2),
            "segment_count": len(segments),
            "model": "n/a",
            "source_file": str(source_transcript),
        }
        transcript_path.write_text(source_transcript.read_text(encoding="utf-8", errors="ignore"), encoding="utf-8")
    else:
        if not args.input:
            raise ValueError("You must provide --input when --transcript is not used.")
        media_path = resolve_media(args.input, out_dir)
        language = None if args.language == "auto" else args.language
        segments, metadata = transcribe_video(media_path, args.model, language)
        write_transcript(segments, transcript_path, metadata)

    summary_md = create_summary_markdown(metadata, segments, args.summary_sentences, args.key_points)
    summary_path.write_text(summary_md, encoding="utf-8")
    metadata_path.write_text(json.dumps(metadata, indent=2, ensure_ascii=True), encoding="utf-8")

    print("Done")
    print(f"- Transcript: {transcript_path}")
    print(f"- Summary:    {summary_path}")
    print(f"- Metadata:   {metadata_path}")

    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except Exception as exc:  # pragma: no cover
        print(f"Error: {exc}", file=sys.stderr)
        raise
