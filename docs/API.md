# HTTP API

开发地址：http://127.0.0.1:8080 。Swagger UI：`/swagger-ui.html`，OpenAPI：`/v3/api-docs`。生产 profile 默认关闭文档。

成功：`{"code":"OK","message":"成功","data":...}`。
错误：`{"code":"INVALID_ARGUMENT","message":"参数无效，请检查题目和选项","data":null}`。

HTTP 200 成功；400 参数或题目/选项关系不合法；404 内容不存在或未开放；405 方法不支持；500 内部错误，响应不泄露 SQL、密码或堆栈。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | /api/subjects | 科目与开放状态 |
| GET | /api/subjects/{id}/chapters | 章节，包含 parentId |
| GET | /api/chapters/{id}/knowledge-points | 知识目录、关系、来源、开放故事链接 |
| GET | /api/knowledge-points/{id} | 知识简介，不返回完整总结 |
| GET | /api/stories/{id} | 当前场景和安全题目投影 |
| POST | /api/stories/{id}/answer | 服务端判分与全部解析 |
| GET | /api/knowledge-cards/{id} | 知识卡与关联故事 |

## 答案边界

剧情查询返回 `question: {id,prompt,options:[{id,label,name}]}`。不会返回正确标记、术语解释、原因、误区、选项反馈或知识总结；实体不会直接序列化到 HTTP。

提交示例：

```http
POST /api/stories/1/answer
Content-Type: application/json

{"questionId":11,"optionId":112}
```

返回 data 包含 storyId、questionId、selectedOptionId、correct、feedback、explanations、summaries。explanations 始终包含当前问题的全部选项，逐项提供 id、label、name、term、plainMeaning、correct、reason、misconception。summaries 从独立知识库读取，并给出 cardId。

客户端不提交判分依据。后端先验证故事开放状态、题目所属故事、选项所属题目，再从数据库选项判分。重复提交不创建服务端学习记录，当前为无副作用查询式判分。

## 范围说明

每个 V0.1 演示故事只有一道决策题，分成场景、反馈解析、总结、完成四个 UI 步骤；不存在完整剧情树或后续问题接口。

知识卡是可公开读取的教学资源。游客“解锁”由浏览器记录控制展示，不是服务端权限保护，也不用于反作弊。没有登录，不能安全证明某个游客已答完题；题目接口本身仍严格不泄露答案。

通过 Vite `/api` 代理实现同源开发；生产部署需将 `/api` 反向代理至后端，前端路由回退到 index.html。没有宽泛的任意来源 CORS 配置。
