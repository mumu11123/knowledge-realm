# V0.2 Java AI Agent 增量方案

## 现状与边界

保留 Vue/Pinia 学习流程、LearningService、MyBatis 与 Flyway；V0.1 的 DEMO/DRAFT 数据不自动升级为可靠知识。原浏览器记录仍保留，不冒充已迁移到后端。

## 新模块

1. `ai/knowledge`：独立 MySQL 知识片段表，保留知识点映射、真实来源 URL、版本、审核人、时间和状态。人工审核前不参与检索。用中文 n-gram/关键词检索实现轻量 RAG，不为少量知识引入向量数据库或额外 embedding API。
2. `ai/auth`：随机不透明 HttpOnly/SameSite 会话 Cookie，数据库仅存 Token 哈希；身份由服务端确认。管理员使用独立环境变量 Bearer Token，普通用户不能调用管理 API。
3. `ai/learning`：核心答题接口仅发布 `LearningAnswerSubmitted` 业务事件，不依赖 AI 包；扩展监听器在原 HTTP 线程解析授权会话并写入真实判分记录。浏览器旧进度不隐式上传；工具只能读取当前会话，复习计划是只读建议。
4. `ai/model`：兼容 Boot 3.5 的 Spring AI 1.1.x、可配置的兼容 Chat Completions API。模型主动返回 tool_calls，后端通过 Spring AI ToolCallingManager 执行业务方法；轮数和工具预算均受限。
5. `ai/tutor`：当前知识点与相关片段检索 → 来源编号 → 模型与工具 → 结构化答案 → 引用白名单/证据校验 → 返回答案、真实来源与工具轨迹。缺资料、未配置、超时、不可用分别降级。
6. `ai/admin`：知识片段审核、结构化故事生成、校验、草稿持久化与人工审批发布；AI 无发布工具。
7. 可观测性：请求 ID、耗时、Token 总量、工具名称、成功/降级原因写入审计表和聚合指标，不保存 Key、Cookie、完整私人提示词或模型私密推理。

## 前端

卡片增加“问问 AI”，展示输入、请求状态、回答、检索来源、工具轨迹与降级原因。独立管理员页面使用临时内存令牌输入，不持久化管理员凭证。

## 验证

单测检索相关性/拒绝无依据、引用伪造、工具上下文隔离、调用预算、结构化草稿校验；真实 MySQL 验证审核、会话与权限。协议测试服务仅用于自动测试，模拟 tool_calls 验证真实 Spring AI 调用闭环，明确不充当生产模型。真实模型端到端需由用户在 .env 配置有效 Key；没有凭证时不虚构验证结果。
