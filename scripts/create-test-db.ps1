. "$PSScriptRoot/load-env.ps1"
if ($env:MYSQL_USER -notmatch '^[A-Za-z0-9_]+$') { throw 'MYSQL_USER 必须仅包含英文字母、数字和下划线。' }
$sql = "CREATE DATABASE IF NOT EXISTS knowledge_realm_test CHARACTER SET utf8mb4; GRANT ALL ON knowledge_realm_test.* TO '$($env:MYSQL_USER)'@'%';"
Push-Location $ProjectRoot
try {
    $sql | & docker compose exec -T mysql sh -c 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql -u root'
    if ($LASTEXITCODE -ne 0) { throw '测试数据库创建失败，请确认 Compose MySQL 已启动。' }
} finally { Pop-Location }
