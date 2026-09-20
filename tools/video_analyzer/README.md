# YouTube Video Analyzer (Transcript + Summary)

This utility automates the workflow we used manually:

1. Get a video from YouTube URL, direct video URL, or local file
2. Transcribe audio with faster-whisper
3. Generate a short extractive summary and key points

Now in v2, the summary output is structured for quick reading:

- Resume Court
- Points Cles
- Actions Concretes
- Timeline

v3 adds automatic EN->FR normalization on generated sections so English videos produce a French-friendly summary.

## Setup

From workspace root:

```powershell
c:/Users/abbac/Documents/GitHub/IA/.venv/Scripts/python.exe -m pip install -r tools/video_analyzer/requirements.txt
```

## Usage

### 1) Analyze from YouTube URL

```powershell
c:/Users/abbac/Documents/GitHub/IA/.venv/Scripts/python.exe tools/video_analyzer/youtube_video_analyzer.py --input "https://www.youtube.com/watch?v=VIDEO_ID" --outdir "tmp/video-analysis"
```

Or with the one-command launcher:

```powershell
./tools/video_analyzer/run-video-analysis.ps1 -InputSource "https://www.youtube.com/watch?v=VIDEO_ID"
```

### 2) Analyze from direct media URL

```powershell
c:/Users/abbac/Documents/GitHub/IA/.venv/Scripts/python.exe tools/video_analyzer/youtube_video_analyzer.py --input "https://.../videoplayback?..." --outdir "tmp/video-analysis"
```

### 3) Analyze from local video file

```powershell
c:/Users/abbac/Documents/GitHub/IA/.venv/Scripts/python.exe tools/video_analyzer/youtube_video_analyzer.py --input "tmp/video.mp4" --outdir "tmp/video-analysis"
```

### 4) Summarize an existing transcript only (fast)

```powershell
c:/Users/abbac/Documents/GitHub/IA/.venv/Scripts/python.exe tools/video_analyzer/youtube_video_analyzer.py --transcript "tmp/video_transcript.txt" --outdir "tmp/video-analysis"
```

## Outputs

Generated inside outdir:

- transcript.txt
- summary.md
- metadata.json

The `summary.md` file is generated in a human-friendly French structure.

## Notes

- Default whisper model is `tiny` for speed.
- For higher quality, use `--model small` (slower):

```powershell
c:/Users/abbac/Documents/GitHub/IA/.venv/Scripts/python.exe tools/video_analyzer/youtube_video_analyzer.py --input "..." --model small --outdir "tmp/video-analysis"
```
run-video-analysis.ps1 -InputSource "https://www.youtube.com/watch?v=VIDEO_ID"