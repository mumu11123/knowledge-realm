CREATE TABLE learner_session (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 token_hash CHAR(64) NOT NULL UNIQUE,
 expires_at TIMESTAMP NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE learner_attempt (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 learner_id BIGINT NOT NULL, story_id BIGINT NOT NULL, question_id BIGINT NOT NULL,
 option_id BIGINT NOT NULL, is_correct BOOLEAN NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(learner_id) REFERENCES learner_session(id),
 FOREIGN KEY(story_id) REFERENCES story(id),
 FOREIGN KEY(question_id) REFERENCES question(id),
 FOREIGN KEY(option_id) REFERENCES question_option(id),
 INDEX ix_attempt_learner(learner_id,created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ai_knowledge_chunk (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, knowledge_id BIGINT NOT NULL,
 title VARCHAR(200) NOT NULL, body TEXT NOT NULL,
 source_url VARCHAR(1000) NOT NULL, source_title VARCHAR(200) NOT NULL,
 source_version VARCHAR(200) NOT NULL,
 review_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
 reviewed_by VARCHAR(100), reviewed_at TIMESTAMP NULL, review_note VARCHAR(1000),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 FOREIGN KEY(knowledge_id) REFERENCES knowledge_point(id),
 CONSTRAINT ck_chunk_review CHECK(review_status IN ('PENDING','REVIEWED','REJECTED')),
 CONSTRAINT ck_chunk_audit CHECK(review_status <> 'REVIEWED' OR (reviewed_by IS NOT NULL AND reviewed_at IS NOT NULL)),
 INDEX ix_chunk_scope(knowledge_id,review_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ai_story_draft (
 id BIGINT PRIMARY KEY AUTO_INCREMENT, knowledge_id BIGINT NOT NULL,
 payload JSON NOT NULL, source_ids JSON NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
 generated_by VARCHAR(100) NOT NULL, reviewed_by VARCHAR(100), reviewed_at TIMESTAMP NULL, review_note VARCHAR(1000),
 published_story_id BIGINT NULL UNIQUE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(knowledge_id) REFERENCES knowledge_point(id),
 FOREIGN KEY(published_story_id) REFERENCES story(id),
 CONSTRAINT ck_ai_draft_status CHECK(status IN ('DRAFT','REJECTED','PUBLISHED')),
 CONSTRAINT ck_ai_draft_publish CHECK(status <> 'PUBLISHED' OR (reviewed_by IS NOT NULL AND reviewed_at IS NOT NULL AND published_story_id IS NOT NULL))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ai_call_log (
 request_id CHAR(36) PRIMARY KEY, learner_id BIGINT NULL,
 operation VARCHAR(30) NOT NULL, status VARCHAR(40) NOT NULL,
 model VARCHAR(100) NOT NULL, elapsed_ms BIGINT NOT NULL,
 prompt_tokens INT NOT NULL, completion_tokens INT NOT NULL,
 tool_names JSON NOT NULL, error_code VARCHAR(80),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(learner_id) REFERENCES learner_session(id),
 INDEX ix_ai_rate(learner_id,created_at), INDEX ix_ai_status(status,created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Curated professional notes remain PENDING until an administrator actually reviews them.
-- Never promote existing DEMO/DRAFT knowledge or stories automatically.
INSERT INTO ai_knowledge_chunk(knowledge_id,title,body,source_url,source_title,source_version) VALUES
(201,'按下标访问：顺序表与单链表','在定长元素与随机访问内存模型下，顺序表的元素连续存放，可以通过首地址和下标计算目标地址，按下标访问为 O(1)。单链表的结点由指针链接，不能由下标直接计算任意结点地址；从头结点沿后继寻找任意下标的最坏时间为 O(n)。逻辑相邻不代表物理地址相邻。','https://opendsa.org/OpenDSA/Books/Catalog/html/ListAnalysis.html','OpenDSA · Comparison of List Implementations','在线参考，2026-10-27 核对；中文笔记待人工审核'),
(201,'插入复杂度的适用条件','单链表在已知插入位置的前驱结点、且结点分配视为常数代价时，修改链接插入为 O(1)。如果还需要从头查找前驱，查找部分最坏为 O(n)。顺序表中间插入通常需要移动后续元素，最坏为 O(n)。不能笼统地说链表插入永远比顺序表快。','https://opendsa.org/OpenDSA/Books/Catalog/html/ListAnalysis.html','OpenDSA · Comparison of List Implementations','在线参考，2026-10-27 核对；中文笔记待人工审核'),
(301,'栈的访问规则','栈只允许在栈顶一端插入和删除元素，遵循后进先出 LIFO。栈是一种操作受限的线性表，不要求必须使用连续存储。只有在全部元素先入栈、期间没有出栈，然后连续出栈的条件下，完整出栈顺序才是入栈顺序的逆序；不能把这一结论推广为任意入栈出栈交错过程。','https://opendsa.org/OpenDSA/Books/Catalog/html/StackArray.html','OpenDSA · Stacks','在线参考，2026-10-27 核对；中文笔记待人工审核'),
(302,'普通队列的访问规则','普通队列只允许在队尾插入、在队头删除，遵循先进先出 FIFO。队列的操作规则与顺序或链式实现分开，不能因为底层使用数组就允许任意位置出队。优先队列按优先级选择元素，不等同于普通 FIFO 队列。','https://opendsa.org/OpenDSA/Books/CS3/html/Queue.html','OpenDSA · Queues','在线参考，2026-10-27 核对；中文笔记待人工审核');
