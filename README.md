# 知境（Knowledge Realm）

> 在故事里，理解知识。

面向计算机考研 408 的移动端互动式学习平台。用生活情境引出问题，通过真实后端判分、逐项解析和独立知识总结，把抽象概念变成可以参与的故事。

V0.2 已在原学习闭环上增量接入 Java AI Agent：DeepSeek、已审核知识检索（RAG）、真实业务工具调用、来源复核、管理员草稿审核与调用观测。三个原故事仍为**原创演示、待审核**，不代表完整考纲覆盖。

## V0.2 AI 快速体验

1. 在根目录 `.env` 配置以下后端环境变量，勿使用 VITE_ 前缀，也勿提交 Key：

```dotenv
AI_ENABLED=true
AI_API_KEY=填写自己的DeepSeek密钥
AI_BASE_URL=https://api.deepseek.com
AI_MODEL=deepseek-flash
AI_TIMEOUT_SECONDS=45
```

2. 启动 MySQL 与后端、前端（下方命令保持适用）。完成一个故事、打开知识卡，点击**问问 AI**。
3. 试问“为什么单链表不能像顺序表一样随机访问？”，核对回答下的来源和逐段原文；也可问“结合我的错题和进度给出复习计划”，查看真实工具轨迹和结果。
4. 管理员运行 `./scripts/init-ai-admin.ps1`，从本地 `.env` 复制 `AI_ADMIN_TOKEN`，重启后端后打开 `/admin/ai`。令牌只在页面内存保留；先审核专业片段，再生成草稿，逐个核对并明确批准后才发布。

首批四段专业笔记已由维护者在 2026-10-27 明确审核批准，V5 迁移仅对原文哈希匹配的 v1 内容记录审批；不批准任何演示故事或未来 AI 草稿。新增知识默认仍需审核。

完整说明：[AI 使用与配置](docs/AI_GUIDE.md) · [增量架构](docs/AI_ARCHITECTURE.md) · [评估与实测](docs/AI_EVALUATION.md) · [首批审核范围](docs/AI_KNOWLEDGE_REVIEW.md)。

## 已实现

- 四门科目入口：数据结构开放，其他三科明确标记即将开放。
- 数据结构七章、30 个知识点基础目录，前置/关联知识、资源与审核状态。
- 三个完整故事：星光书屋（顺序表与单链表）、云朵咖啡馆（栈）、森林小站（队列）。
- 场景 → 选择 → 分支反馈与全部选项解析 → 专业总结 → 完成与解锁卡片。
- 服务端验证故事、题目和选项关系，服务端判分；查询题目不泄露答案和后续反馈。
- 知识卡翻转、收藏、关联故事返回和简单的 24 小时复习提醒。
- 游客本地学习记录、最近访问、完成故事、接触知识点与解锁卡片统计。
- 移动优先蓝紫界面、本地 SVG 插画、键盘操作、减少动画偏好、加载/错误/重试/空状态。

“已完成”仅表示读完演示流程，不代表熟练掌握。进度和收藏保存在当前浏览器，不跨设备同步。V0.2 为本浏览器建立服务端授权会话，并记录后续真实答题供 AI 工具查询，旧本地记录不自动上传。“我的学习”可单独清除本地记录或当前 AI 会话。重新进入或刷新故事从题目开始，已完成状态与卡片保留。

## 目录

```text
frontend/                     Vue 3 + TypeScript + Vite
  src/api/                    Axios 与明确的接口类型
  src/views/                  首页、地图、剧情、卡片、我的学习
  src/components/             SVG 图标、场景插画、异步状态
  src/storage/                版本化游客存储适配器和进度逻辑
  src/stores/                 Pinia 学习状态
  tests/                      Vitest 与 Playwright 测试
backend/                      Java 21 / Spring Boot 3.5 / Maven
  src/main/java/com/knowledge/realm/
    controller/ service/ mapper/ entity/ dto/ vo/ common/ exception/
  src/main/resources/db/migration/  Flyway 结构和演示数据
  src/test/                   判分测试与真实 MySQL 集成测试
docs/                         需求、计划、API、数据库、AI 扩展与验收
scripts/                      Windows 环境初始化、启动和测试辅助
.github/workflows/ci.yml       Java 21 + MySQL 8.4 + 浏览器检查
```

## 环境

- JDK 21（代码编译目标），Maven 3.6.3+。
- Node.js 22.12+，推荐 Node 24 LTS；npm。
- Docker Desktop，或独立的 MySQL 8 服务。
- Git。浏览器测试需要 Playwright Chromium 或本机 Edge。

具体版本与官方兼容依据见 [依赖说明](docs/DEPENDENCIES.md)。

## Windows 快速开始

以下命令均在仓库根目录执行。先克隆仓库：

```powershell
git clone https://github.com/mumu11123/knowledge-realm.git
cd knowledge-realm
```

### 1. 创建本地配置并启动 MySQL

```powershell
./scripts/init-env.ps1
docker compose up -d mysql
docker compose ps
```

初始化脚本从 `.env.example` 生成 `.env`，使用随机本地密码，不覆盖已有配置。也可以自行复制示例并填写密码；`MYSQL_PASSWORD` 与 `DB_PASSWORD` 必须相同。`.env` 不提交 Git。

Compose 默认把 MySQL 映射到 `127.0.0.1:3307`，数据库 `knowledge_realm`，用户 `realm`。密码由 `.env` 提供。等待状态 healthy 后启动后端。数据保存在命名卷 `realm_mysql`，普通停止不会删除数据：

```powershell
docker compose stop mysql
```

如果已有 MySQL 8，可跳过 Compose：自行创建 UTF-8 数据库与专用用户，授予该库权限，再调整 `.env` 的 `DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USERNAME`、`DB_PASSWORD`。不要把本地实例和 Compose 同时放到 3307 端口。

### 2. 启动后端

```powershell
./scripts/start-backend.ps1
```

脚本加载根目录 `.env`，在 backend 执行 Maven；首次下载依赖可能较慢。Flyway 自动创建表并写入演示数据，无需手动导入 SQL。

- 科目接口：[http://127.0.0.1:8080/api/subjects](http://127.0.0.1:8080/api/subjects)
- Swagger：[http://127.0.0.1:8080/swagger-ui.html](http://127.0.0.1:8080/swagger-ui.html)

### 3. 启动前端

新开一个终端：

```powershell
cd frontend
npm ci
npm run dev
```

打开 [http://127.0.0.1:5173](http://127.0.0.1:5173)。Vite 将 `/api` 请求代理到后端；可通过根目录 `.env` 的 `VITE_API_TARGET` 修改后端地址。

### 4. 体验一个完整故事

首页“开始探索” → 线性表 → “星光书屋的寻书委托” → 选择任意选项并提交 → 阅读对应反馈，展开 A/B/C/D 全部解析 → “带走这一段知识” → 阅读总结 → “完成学习，解锁知识卡” → 打开并翻转卡片 → 收藏、记录复习 → 刷新页面检查“我的学习”。

顺序表故事选 A 为正确分支，选 B/C/D 可体验对应错误反馈；咖啡馆选 B、小站选 A 为正确分支。以上答案仅用于开发验收。

## macOS / Linux

先复制 `.env.example` 为 `.env`，设置两组随机密码，保证 MYSQL_PASSWORD 与 DB_PASSWORD 一致。启动 Docker 后：

```bash
docker compose up -d mysql
set -a
. ./.env
set +a
cd backend
mvn spring-boot:run
```

另开终端进入 frontend 执行 `npm ci && npm run dev`。PowerShell 辅助脚本不是 Linux 必需依赖。

## 测试和构建

### 后端业务测试

```powershell
./scripts/test-backend.ps1
```

只运行独立业务测试，MySQL 集成类明确跳过；也可在 backend 执行 `mvn test`。

### 真实 MySQL 集成测试

Compose MySQL 启动后，创建独立测试库并授权：

```powershell
./scripts/create-test-db.ps1
./scripts/test-backend.ps1 -MySql
```

已有独立 MySQL 时自行创建 `knowledge_realm_test` 并授权给 `.env` 的 DB_USERNAME。测试不会把开发库当测试库，测试类固定连接 knowledge_realm_test。验证 Flyway、目录、全部 12 个选项分支、答案隔离、非法请求、卡片、OpenAPI 与审核约束。

### 前端测试和生产构建

```powershell
cd frontend
npm test
npm run build
```

构建结果在 `frontend/dist`。

### 浏览器验收

保持后端与 MySQL 已启动。在 frontend：

```powershell
npx playwright install chromium
npm run test:e2e
```

测试自动启动前端，也可复用已运行的 5173 服务。320px、390px 与桌面验证完整学习、正确/错误反馈、锁定卡片、刷新持久化、网络错误恢复、基本可访问性和键盘；另检查 430px 与横屏不横向溢出。

已有 Microsoft Edge 的 Windows 环境也可运行：

```powershell
$env:PLAYWRIGHT_CHANNEL='msedge'
npm run test:e2e
```

`.github/workflows/ci.yml` 配置 Java 21、MySQL 8.4 与 Chromium 检查。实际本机执行结果和验证边界见 [验收记录](docs/VERIFICATION.md)，不把仅写了配置的 CI 当作已通过结果。

## 配置与部署边界

开发默认 profile 为 dev，生产设置 `SPRING_PROFILES_ACTIVE=prod`，并提供 `DB_URL`（完整 MySQL JDBC URL）、`DB_USERNAME`、`DB_PASSWORD`。生产文档接口默认关闭。应用层没有硬编码数据库密码。

本轮交付本地可运行版本，没有部署公网。部署时用反向代理提供静态资源、`/api` 转发、前端路由回退和 HTTPS；不要使用 Vite 开发服务器作为生产 Web 服务。

知识卡是公开教学资源，游客解锁仅为本地体验状态，不是权限保护或反作弊机制。

## 尚未实现与下一步

当前未实现注册登录与账户云同步、真实排行榜、多问题剧情树、复杂算法动画、完整四科课程或向量检索。管理员页面目前只覆盖知识审核、AI 草稿与人工发布，不是完整内容管理后台。其余知识点仍显示暂无教学内容。

优先建议：为三个演示故事完成教材/考纲核对与人工审核，扩充已审核知识与练习，然后把当前浏览器授权会话升级为正式账户体系。AI 后续设计见 [未来 AI Agent](docs/FUTURE_AI_AGENT.md)。

更多文档：[原始需求](docs/PROJECT_REQUIREMENTS.md) · [实施计划](docs/IMPLEMENTATION_PLAN.md) · [数据库](docs/DATABASE.md) · [API](docs/API.md) · [开发规范](AGENTS.md)。
