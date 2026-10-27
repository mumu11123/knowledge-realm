. "$PSScriptRoot/load-env.ps1"
if ($env:AI_ADMIN_TOKEN -and $env:AI_ADMIN_TOKEN.Length -ge 32) { Write-Output '管理员令牌已配置，未做修改。'; exit 0 }
$bytes=New-Object byte[] 32
$rng=[Security.Cryptography.RandomNumberGenerator]::Create()
try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
$adminToken=([BitConverter]::ToString($bytes)).Replace('-','')
$text=[IO.File]::ReadAllText((Join-Path $ProjectRoot '.env'))
if($text -match '(?m)^AI_ADMIN_TOKEN=.*$') { $text=[regex]::Replace($text,'(?m)^AI_ADMIN_TOKEN=.*$',"AI_ADMIN_TOKEN=$adminToken") }
else { $text+="`nAI_ADMIN_TOKEN=$adminToken`n" }
[IO.File]::WriteAllText((Join-Path $ProjectRoot '.env'),$text,[Text.UTF8Encoding]::new($false))
Write-Output '已生成本地管理员令牌，保存在 .env 的 AI_ADMIN_TOKEN；未输出令牌。重启后端后生效。'
