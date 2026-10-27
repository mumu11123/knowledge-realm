$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$target = Join-Path $projectRoot '.env'
if (Test-Path -LiteralPath $target) { throw '.env 已存在，未做修改。' }
function New-LocalPassword {
    $bytes = New-Object byte[] 24
    $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
    try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
    return ([BitConverter]::ToString($bytes)).Replace('-', '')
}
$password = New-LocalPassword
$rootPassword = New-LocalPassword
$content = Get-Content -LiteralPath (Join-Path $projectRoot '.env.example') -Raw
$content = $content.Replace('MYSQL_PASSWORD=', "MYSQL_PASSWORD=$password").Replace('MYSQL_ROOT_PASSWORD=', "MYSQL_ROOT_PASSWORD=$rootPassword").Replace('DB_PASSWORD=', "DB_PASSWORD=$password")
[IO.File]::WriteAllText($target, $content, [Text.UTF8Encoding]::new($false))
Write-Output '已生成 .env 与随机本地密码；该文件已被 Git 忽略。'
