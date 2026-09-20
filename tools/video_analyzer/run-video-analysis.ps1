param(
    [Parameter(Mandatory = $false)]
    [string]$InputSource,

    [Parameter(Mandatory = $false)]
    [string]$OutDir = "c:/Users/abbac/Documents/GitHub/IA/tmp/video-analysis",

    [Parameter(Mandatory = $false)]
    [string]$Model = "tiny"
)

$pythonExe = "c:/Users/abbac/Documents/GitHub/IA/.venv/Scripts/python.exe"
$scriptPath = "c:/Users/abbac/Documents/GitHub/IA/tools/video_analyzer/youtube_video_analyzer.py"

if ([string]::IsNullOrWhiteSpace($InputSource)) {
    Write-Host "Usage: ./tools/video_analyzer/run-video-analysis.ps1 -InputSource '<youtube_url_or_video_path>'"
    exit 1
}

& $pythonExe $scriptPath --input $InputSource --outdir $OutDir --model $Model
