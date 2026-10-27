-- Explicit maintainer approval in the project chat on 2026-10-27, limited to these four exact notes.
-- Hash guards prevent approval from being inherited by different or edited content.
UPDATE ai_knowledge_chunk
SET review_status='REVIEWED', reviewed_by='项目维护者（2026-10-27聊天确认）',
    reviewed_at=CURRENT_TIMESTAMP,
    review_note='维护者已阅读 docs/AI_KNOWLEDGE_REVIEW.md 并明确同意四段 v1 专业笔记用于检索；不批准任何演示或AI故事。',
    source_version=REPLACE(source_version,'中文笔记待人工审核','中文笔记 v1')
WHERE review_status='PENDING' AND (
 (id=1 AND SHA2(body,256)='f5d298c10b303a4ab23a1eba4a21e0fa7ccc7a0f4217eb2cbd335e11d02f9ba1') OR
 (id=2 AND SHA2(body,256)='f126f805891e12557334cb0644ec6e7a97f8ef1a6de603a7a679621c5057e4b9') OR
 (id=3 AND SHA2(body,256)='b89652fd7dce2fe372a5d21690e66fa35e96d0a4f7aaa8f1a013ee3ad14996da') OR
 (id=4 AND SHA2(body,256)='7892c0c84b8765c666555ce3e85000ff412f6333852f00853db4c718dde8b410')
);
