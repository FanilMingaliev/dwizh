Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot
& .\gradlew.bat ":app:assembleDebug" @args
exit $LASTEXITCODE
