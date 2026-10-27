param([switch]$MySql)
. "$PSScriptRoot/load-env.ps1"
$env:RUN_MYSQL_TESTS = if ($MySql) { 'true' } else { 'false' }
if ($MySql) { Write-Output 'MySQL 集成测试使用独立数据库 knowledge_realm_test，请预先创建并授权给 DB_USERNAME。' }
Push-Location (Join-Path $ProjectRoot 'backend')
try {
    & mvn.cmd "-Dmaven.repo.local=$ProjectRoot/.m2-cache" test
    if ($LASTEXITCODE -ne 0) { throw '后端测试失败，请查看测试报告。' }
} finally { Pop-Location }
