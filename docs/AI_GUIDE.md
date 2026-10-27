# V0.2 AI 学习助手

## 配置

Java 21 编译目标、Spring Boot 3.5.16、Spring AI 1.1.8。官方 [Spring AI 1.1.8 发布说明](https://spring.io/blog/2026/06/12/spring-ai-1-1-8-1-0-9-avaialble-now/) 基于 Boot 3.5 系列；不升级原应用到 Boot 4，不引入向量数据库或额外 embedding 账单。

在根目录 `.env` 设置：

```dotenv
AI_ENABLED=true
AI_API_KEY=自己的DeepSeekKey
AI_BASE_URL=https://api.deepseek.com
AI_MODEL=deepseek-flash
AI_TIMEOUT_SECONDS=45
AI_ADMIN_TOKEN=管理员操作需要至少32字符的独立令牌
AI_COOKIE_SECURE=false
```

管理员令牌可通过 `./scripts/init-ai-admin.ps1` 随机生成，不会打印或覆盖有效令牌。生产 profile 强制 Secure Cookie，必须使用 HTTPS。Key 与管理员令牌都只在后端使用。

运行：`./scripts/start-backend.ps1`；前端 `npm ci`、`npm run dev`。修改配置后重启后端。新 JAR 为 `backend/target/realm-0.2.0.jar`。

DeepSeek 当前 [官方接口文档](https://api-docs.deepseek.com/) 推荐 `deepseek-flash`。本次用户配置的 `deepseek-chat` 仍被接口接受，实际返回 Flash 模型；新配置推荐当前模型名。基址不要重复追加 chat/completions。使用兼容 Chat Completions 的同步非思考模式，显式传入 thinking=disabled，以支持 Spring AI 1.1 的多轮工具结果回传。工具协议依据 [DeepSeek Tool Calls](https://api-docs.deepseek.com/guides/tool_calls/) 与 [Spring AI 1.1 Tool Calling](https://docs.spring.io/spring-ai/reference/1.1/api/tools.html)。

## 数据与审核

- V4 创建独立 ai_knowledge_chunk，初始专业笔记 PENDING；不查询任何故事文字作为检索依据。
- 用户于 2026-10-27 明确审核首批四段中文笔记。V5 以 SHA-256 限定审批原文；不自动审批后续新增/修改的内容，也不升级原 DEMO/DRAFT 课程。
- 管理员 `/admin/ai` 可以查看全文与真实来源、填写审核人和说明，批准或撤销知识片段。
- 专业定义来自已审核片段，不能在生成请求里用自定义定义替换可信知识。
- 模型生成内容仅写入 ai_story_draft，状态 DRAFT。校验四个独立选项、唯一正确答案、全部解析、有限长度、知识映射和原文依据。人工填写审核说明并明确批准后，在一个事务中发布到现有 story/question/option 表。不会自动发布，也不允许重复发布。

## 真实 RAG 与工具调用

1. 校验服务端会话和请求长度，限定当前知识点及显式关联知识。
2. 从 MySQL 只查询 REVIEWED 片段（最多 200 个候选），用中文二元字组/英文词进行相关性排序，取最多四个片段。本阶段为词法检索，不使用 embeddings 或向量库；近义表达可能检索失败，失败时明确拒绝猜测。
3. 给真实片段分配 K 编号，传入当前问题上下文。模型不能提供自造 URL。
4. Spring AI ChatModel 发送真实工具定义，读取提供商 tool_calls，使用 ToolCallingManager 执行注册的业务方法并回传结果；不是在 Prompt 中伪造调用。
5. 每条回答必须给出实际片段编号和连续原文。后端白名单校验来源、逐字检查原文，再由独立的模型调用复核解释是否得到来源支持；失败时拦截回答。
6. 返回专业解释、原文依据、真实来源、工具轨迹、原始工具结果、耗时与提供商 Token 用量。前端对输出转义，不执行模型 HTML。

工具：getKnowledgePoint、getRelatedKnowledgePoints、getLearningProgress、getWrongKnowledgePoints、generateReviewPlan。后面三者调用 AuthorizedLearningService，用户 ID 只来自服务端 ToolContext，不在模型参数 schema 中；所有工具只读，没有发布或修改进度的工具。

## 授权与隐私

当前采用**授权浏览器游客会话**，不是注册账户。服务端发放 256-bit 随机 HttpOnly/SameSite=Strict Cookie，数据库只存 SHA-256 哈希，30 天有效。不同浏览器/会话的数据彼此隔离；不接受请求或模型指定其他用户 ID。

建立会话后，后续提交答案把真实服务端判分结果写入 learner_attempt。V0.1 本地进度不隐式上传，也不能作为可信错题伪造进服务器。统计是提交次数/正确次数/涉及故事，不把它宣称为掌握率或已完成学习。

AI 提问与按需调用工具读取到的当前会话数据会发送至配置的模型提供商；页面有明确提示。管理员令牌与会话 Token 不发给模型。`DELETE /api/ai/session` 只删除当前会话的答题记录，撤销 Cookie，并将调用日志去关联；本地卡片和收藏保留。过期会话不能再读取数据，数据库过期数据的定期清理仍属于后续运营工作。

管理员使用独立 Bearer Token，所有知识审批、草稿与观测 API 都校验该 Token。不提供完整账户 RBAC；公网多用户部署前应升级身份系统，并结合提供商预算控制与网关防滥用。

## 限制与降级

- 每次最多 1000 字提问，四个模型工具轮次、八次工具调用，最多四个并发任务，单轮输出最多 2000 Token。
- 总请求默认 45 秒，允许配置 2–90 秒；HTTP 连接 5 秒、单次读取最多 30 秒。无自动重试，避免成倍账单。取消后不再执行新工具。
- 完成调用日志用于每个会话每分钟 10 次的窗口限流，管理员草稿每分钟 3 次；并发任务另有全局硬上限。当前不是跨实例分布式限流。
- NO_RELIABLE_KNOWLEDGE、MODEL_NOT_CONFIGURED、MODEL_TIMEOUT、MODEL_UNAVAILABLE、UNVERIFIED_ANSWER、BUSY 分开表示。降级只展示真实检索资料，不生成假答案或假工具轨迹。
- 精确引用与模型复核不能数学保证语义正确；专业内容和生成故事仍需人工审核。近义检索、长对话记忆、流式回答和向量检索暂不实现。

## HTTP 接口

所有接口沿用 `{code,message,data}` 包装。

| 方法 | 路径 | 访问 |
| --- | --- | --- |
| POST | /api/ai/session | 创建/复用当前浏览器会话，不返回 Token |
| DELETE | /api/ai/session | 当前授权会话清除自身记录 |
| GET | /api/ai/status | 只返回配置是否就绪与模型名 |
| GET | /api/ai/progress | 当前授权会话 |
| GET | /api/ai/knowledge/{id}/sources | 当前授权会话，已审核片段 |
| POST | /api/ai/tutor | 当前授权会话，`{knowledgeId,message}` |
| GET | /api/admin/ai/chunks | 管理员 |
| POST | /api/admin/ai/chunks/{id}/review | 管理员，`{reviewer,note,approve}` |
| GET / POST | /api/admin/ai/drafts | 管理员；生成参数 knowledgeId、constraints、difficulty、style |
| POST | /api/admin/ai/drafts/{id}/publish | 管理员，`{reviewer,note,approve:true}` |
| GET | /api/admin/ai/observations | 管理员，最近 50 次脱敏调用日志 |

## 可观测性

ai_call_log 保存请求编号、操作、状态、配置模型、耗时、输入/输出 Token、工具名称、错误类别与时间；包括证据复核调用的 Token。不给日志写 Key、Cookie、完整提问、私人模型回答或私密推理。管理员观测接口可定位请求编号。

Micrometer 提供 realm.ai.calls、realm.ai.duration、realm.ai.tokens、realm.ai.retrieval.matches。Actuator 对外只开放无详情 health；没有开放 env、全部 metrics 或数据库配置。

测试和真实演示记录见 [AI_EVALUATION.md](AI_EVALUATION.md)。
