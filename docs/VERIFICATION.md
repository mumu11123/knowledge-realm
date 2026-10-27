# V0.1 验收记录

> V0.2 AI Agent 的最新验收见 [AI_EVALUATION.md](AI_EVALUATION.md)。以下保留 V0.1 的历史结果。

执行日期：2026-10-27。以下是实际本机结果，不把未执行的 CI 当作通过。

## GitHub Actions 补充验证

[首次完整 CI](https://github.com/mumu11123/knowledge-realm/actions/runs/40454344950) 已实际通过：Linux、Java 21、MySQL 8.4、Node 24、Chromium；包含后端测试、前端格式检查/单测/构建和全部浏览器验收。此结果补充了本机缺少 Java 21 与 Docker 引擎的验证边界。

## 环境

Windows，Java 24.0.1（编译 release 21）、Maven 3.9.6、Node 24.15.0、npm 11.12.1、MySQL 8.0.30、Microsoft Edge Chromium。

本次在项目 `.local/mysql-data` 创建独立 MySQL 实例，绑定 127.0.0.1:3307。开发库 knowledge_realm 和测试库 knowledge_realm_test 分开；使用随机密码，仅存放在 Git 忽略的 `.env`。未修改机器已有 MySQL 数据目录。

## 实际结果

| 检查 | 结果 |
| --- | --- |
| Vue/TypeScript 类型检查与生产构建 | 通过 |
| Vitest 本地学习记录逻辑 | 11/11 通过 |
| 后端独立业务测试 | 7/7 通过 |
| Spring Boot + 真实 MySQL 集成测试 | 5/5 通过，无跳过 |
| Maven 可执行 JAR 打包 | 通过 |
| 打包 JAR 启动与真实接口读取 | 通过，8080 正常响应 |
| Flyway 空库建表与演示数据初始化 | 通过，V1/V2；V3 来源修订也已应用 |
| 3 个故事全部 12 个选项服务端判分 | 通过 |
| 提交前响应无答案、原因、反馈和总结字段 | 通过 |
| 跨故事题目、跨题目选项、缺字段、无效 ID | 通过，返回正确的 400/404 |
| 未审核内容不能直接标记正式发布 | 通过，MySQL CHECK 拒绝更新 |
| Swagger/OpenAPI | 通过，真实接口返回文档 |
| Playwright 浏览器学习流程与可访问性 | 12/12 通过 |
| Docker Compose 配置解析 | `docker compose config --quiet` 通过 |

浏览器测试使用真实 Spring Boot API 与 MySQL；只有“网络失败后重试”测试主动阻断一个请求来检查错误状态，没有用模拟响应代替教学数据。

覆盖 320px、390px 和 1440px；另检查 430px 和 844×390 横屏不横向溢出。验证：科目状态 → 地图 → 进入故事 → 错误选择 → A/B/C/D 全部解析 → 总结 → 完成 → 卡片翻转 → 收藏 → 复习 → 刷新恢复 → 我的学习。另验证栈故事正确反馈、未解锁卡片入口、后端断连时显示重试且不伪造内容。

axe 检查主要页面、答案反馈和已解锁卡片的 WCAG A/AA 自动规则，无报告违规；同时验证 radio 键盘操作与减少动画偏好。自动扫描不等同于完整人工无障碍认证。

截图已实际检查首页、剧情、解析与卡片，以及窄屏与桌面布局；本机截图位于 `.local/ui`，不提交生成产物。

## 尚未验证或未实现

- Docker 引擎在本次环境无法连接，未执行 Compose 启动 MySQL 8.4；实际数据库验收使用本机独立 MySQL 8.0.30。
- 本机没有 JDK 21，实际运行与测试使用受 Spring Boot 3.5 支持的 JDK 24；字节码编译目标为 Java 21。CI 已配置 JDK 21，远程状态以 GitHub Actions 为准。
- 未部署公网，未进行负载测试、真实 iOS Safari / Android 真机测试；手机尺寸测试使用 Edge Chromium。
- 不包括注册登录、账户同步、AI、管理员后台、多问题剧情树、复杂算法动画或完整四科内容。
- 三个故事均为演示和待审核，来源可追溯，但未完成指定教材/考纲的人工审核。
