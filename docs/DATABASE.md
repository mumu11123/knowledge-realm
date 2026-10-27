# 数据库设计

MySQL 8 / InnoDB / utf8mb4。Flyway V1 建表、V2 初始化演示数据、V3 明确来源链接；不使用 ORM 自动建表。

V0.2 增量迁移：V4 新增 learner_session（Token 哈希/有效期）、learner_attempt（授权会话真实答题）、ai_knowledge_chunk（独立专业片段与审核记录）、ai_story_draft（结构化草稿与人工发布记录）、ai_call_log（脱敏调用观测）；V5 记录维护者对首批四段专业笔记的明确审批，以原文 SHA-256 限定适用范围。未修改 V1–V3，也不把故事作为检索库。

V6 新增 learning_resource 与 knowledge_resource：外部视频元数据及多对多知识点映射。初始 44 条均来自「蓝不过海呀」公开合集，平台/作者/URL/核对日期可追溯；内容审核状态为 NOT_REVIEWED，与链接归属核对分开。资源链接不会改变原知识点的 EMPTY/DEMO 状态、解锁卡片或自动加入 ai_knowledge_chunk。

| 表 | 用途与关系 |
| --- | --- |
| subject | 四门科目，OPEN / COMING_SOON |
| chapter | 章节，subject_id 外键，parent_id 自引用，可继续细分 |
| knowledge_point | 独立知识库；定义、通俗解释、条件化结论与误区 |
| knowledge_relation | PREREQUISITE / RELATED；复合主键，禁止自身关联 |
| story | 原创场景与初始剧情，单独保存内容状态 |
| story_knowledge | 故事与知识点多对多，双向索引 |
| question | 故事中的问题，故事内顺序唯一；V0.1 服务仅支持单问题故事 |
| question_option | 每个选项独立保存术语、解释、判分、原因、误区和剧情反馈 |
| knowledge_card | 知识卡与知识点关联；展示内容来自独立知识库 |

科目代码、章节名称、知识名称、选项标签、题目顺序和知识卡所属知识点都有对应唯一约束；所有关系有外键。按科目读取章节、反查知识关联故事有专用索引。

## 内容治理

知识点、故事、选项与卡片保存 content_source、syllabus_version、review_status 和创建/更新时间。

- EMPTY：知识目录占位，没有开放资源。
- DEMO：演示资源，可访问，但界面必须标记待审核。
- PUBLISHED：正式发布，知识点和故事的数据库 CHECK 要求 REVIEWED。
- DRAFT / REVIEWED：审核状态，与资源是否制作分开。

初始化包含四科入口、数据结构七章、30 个知识点目录、三个完整演示故事、12 个选项、三张知识卡。目录仅为基础框架，不声称覆盖指定年份完整考纲。其余三科没有虚构章节或学习资源。

## 游客数据

V0.1 无账户。进度、收藏与简单复习不写入教学数据库，封装在浏览器的版本化存储适配器中。故事是否完成由用户读完总结并点击完成记录，不能仅凭进入故事推断完成或掌握。

## 迁移与测试

后端启动自动执行未应用迁移。已应用的迁移不可修改；后续新增 V3 等文件。集成测试使用独立的 knowledge_realm_test 数据库并运行相同迁移，不使用 H2。

本地 MySQL 8.0.30 已实际完成空库迁移与测试；Compose 默认镜像为 MySQL 8.4 LTS，当前机器 Docker 引擎无法连接，Compose 启动尚未在本机验证。
