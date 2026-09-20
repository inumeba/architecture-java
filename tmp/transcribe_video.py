from faster_whisper import WhisperModel
from pathlib import Path

video_path = Path(r"c:/Users/abbac/Documents/GitHub/IA/tmp/video.mp4")
out_txt = Path(r"c:/Users/abbac/Documents/GitHub/IA/tmp/video_transcript.txt")

model = WhisperModel("small", device="cpu", compute_type="int8")
segments, info = model.transcribe(str(video_path), language="en", vad_filter=True)

with out_txt.open("w", encoding="utf-8") as f:
    f.write(f"# language={info.language} prob={info.language_probability:.4f}\n")
    for seg in segments:
        f.write(f"[{seg.start:8.2f} -> {seg.end:8.2f}] {seg.text.strip()}\n")

print(out_txt)
