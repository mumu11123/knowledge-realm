$ErrorActionPreference = 'Stop'
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $ProjectRoot '.env'
if (!(Test-Path -LiteralPath $envFile)) { throw '缺少 .env。先运行 ./scripts/init-env.ps1 或复制 .env.example 并填写密码。' }
Get-Content -LiteralPath $envFile | ForEach-Object {
    if ($_ -match '^([A-Z_][A-Z0-9_]*)=(.*)$') {
        [Environment]::SetEnvironmentVariable($matches[1], $matches[2].Trim(), 'Process')
    }
}
