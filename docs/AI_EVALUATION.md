# V0.2 AI 评估与实测

日期：2026-10-27。模型测试与协议模拟分开记录，默认自动测试不使用真实 Key、不产生云模型费用。

[GitHub Actions 完整验收](https://github.com/mumu11123/knowledge-realm/actions/runs/40464214476) 已通过 Java 21、MySQL 8.4、Node 24 与 Chromium 的后端、前端和浏览器检查。后续将核心答题与 AI 记录连接改为业务事件后，本机 33 项后端测试也再次全部通过。

## 已实际执行

| 检查 | 结果 |
| --- | --- |
| 全部后端测试（含真实 MySQL） | 33/33 通过，无跳过 |
| 前端进度单测 | 11/11 通过 |
| 前端类型检查、生产构建 | 通过 |
| 默认 Playwright 套件 | 18/18 通过，320px/390px/桌面 |
| 真实 DeepSeek 最小连接 | 成功，接口实际返回 deepseek-flash |
| 真实 DeepSeek RAG | ANSWERED，返回 K1/K2 与逐字原文依据 |
| 真实 DeepSeek 工具调用 | 执行 getLearningProgress、getWrongKnowledgePoints、generateReviewPlan |
| 真实模型草稿生成 | 草稿 #1《书架上的索引卡》，DRAFT，四选项、一个正确答案；未发布 |
| 最新移动端真实模型完整流程 | ANSWERED，约 3.98 秒，3322 输入 + 414 输出 Token（含证据复核） |
| 真实回答页面可访问性与窄屏溢出 | axe A/AA 自动检查通过，无横向溢出 |

计时、Token 数属于具体样例，不是性能承诺。数据来自真实提供商响应与业务调用日志。截图、完整测试输出和该合成测试会话的结果保存在本机 `.local/ai-ui` / `.local`，不提交私人运行产物。

工具测试先在独立浏览器会话里向真实答题 API 提交错误选项，随后模型通过工具读到：1 次提交、0 次正确、知识点 201 的 1 次错误，并生成对应只读复习建议。数据不是前端伪造，也没有读取其他会话。

## 可重复运行

```powershell
# 根目录；独立 MySQL 测试库已创建并授权后
./scripts/test-backend.ps1 -MySql

cd frontend
npm test
npm run format:check
npm run build
$env:PLAYWRIGHT_CHANNEL='msedge' # 或先安装 Playwright Chromium
npm run test:e2e
```

显式真实模型验收（会产生少量费用，保持后端/数据库/前端已启动并配置 Key）：

```powershell
cd frontend
$env:PLAYWRIGHT_CHANNEL='msedge'
npm run test:live-ai
```

默认 CI 没有 Key，真实测试数据库照常检索已审核片段，验证 MODEL_NOT_CONFIGURED 与真实来源显示；不会启用假的生产模型服务。`test:live-ai` 才执行完整外部模型调用。

## 检索评估集

`backend/src/test/resources/ai-rag-evaluation.json` 包含 10 个案例：顺序/链式随机访问、插入条件、查找前驱代价、LIFO、交错出栈、FIFO、普通/优先队列、基于错题的计划，以及天气和未制作章节的拒答。

测试在独立测试事务里显式配置审核状态，检查预期来源是否命中、离题/无内容问题是否返回空。测试夹具不会批准或发布生产数据。当前 10/10 达到预期覆盖和拒绝行为；不是大规模通用准确率声明。

## 安全与降级用例

- 两个真实会话 A/B：A 的答题不出现在 B 的统计；伪造 userId/learnerId 查询参数不能改变身份。
- 未认证的 tutor/progress/会话删除返回 401；普通会话与错误管理员令牌不能审核或生成草稿。
- 工具 schema 没有用户身份字段，只注册五个只读方法。模型在实际 HTTP tool_calls 中伪造 learnerId=999，执行器仍以服务端身份 17 读取真实业务服务。
- PENDING/REJECTED 片段不进 RAG；撤销审核后立即从后续检索排除。
- 伪造来源编号、虚构原文、注入外部 URL、语义复核不支持的解释都被拦截。
- 无依据不调用模型；提供商异常、HTTP 503 协议错误与整体超时均返回明确降级，保留真实检索结果，不伪造答案。
- 整体超时取消任务，工具预算超过八次或取消后不再执行工具；请求长度与频率有测试。
- 生成草稿不能拥有多个正确答案；未经明确人工批准不能发布；事务发布后不能重复发布。
- 删除 A 会话只撤销 A，B 仍可访问；本地收藏不被服务端清除操作改变。

协议测试使用 JDK HttpServer 提供测试专用 Chat Completions 响应，以真实 Spring AI HTTP 客户端、ToolCallingManager 和业务 Service 验证回路。它不在主代码或运行配置中，不作为真实模型成功的证据；上方真实 DeepSeek 样例单独验证了外部模型链路。

## 进一步评估

当前已验证来源存在、引用逐字匹配与语义复核拒绝流程，但模型复核本身仍是概率判断，不能替代专业人工评审。应继续扩充近义提问、跨知识点问题、提示注入、复杂度条件缺失和长问答样本，并分别统计检索召回、来源精确率、拒答准确率、人工事实评分、延迟与 Token 成本。

尚未开展高并发/多实例压测、真实手机 Safari 测试、长期对话评估、完整四科知识准确率评估；没有实现向量检索或账户 RBAC。
