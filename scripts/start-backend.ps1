. "$PSScriptRoot/load-env.ps1"
Push-Location (Join-Path $ProjectRoot 'backend')
try {
    & mvn.cmd "-Dmaven.repo.local=$ProjectRoot/.m2-cache" spring-boot:run
    if ($LASTEXITCODE -ne 0) { throw '后端启动失败，请查看上方日志。' }
} finally { Pop-Location }
