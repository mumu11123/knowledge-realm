CREATE TABLE subject (
 id BIGINT PRIMARY KEY, code VARCHAR(50) NOT NULL UNIQUE, name VARCHAR(100) NOT NULL,
 description VARCHAR(500) NOT NULL, availability VARCHAR(20) NOT NULL,
 sort_order INT NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 CONSTRAINT ck_subject_availability CHECK (availability IN ('OPEN','COMING_SOON'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE chapter (
 id BIGINT PRIMARY KEY, subject_id BIGINT NOT NULL, parent_id BIGINT NULL,
 name VARCHAR(100) NOT NULL, sort_order INT NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 CONSTRAINT fk_chapter_subject FOREIGN KEY(subject_id) REFERENCES subject(id),
 CONSTRAINT fk_chapter_parent FOREIGN KEY(parent_id) REFERENCES chapter(id),
 UNIQUE KEY uk_chapter_name(subject_id, name), INDEX ix_chapter_order(subject_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE knowledge_point (
 id BIGINT PRIMARY KEY, chapter_id BIGINT NOT NULL, name VARCHAR(100) NOT NULL,
 introduction TEXT NOT NULL, definition TEXT NOT NULL, plain_explanation TEXT NOT NULL,
 key_conclusions TEXT NOT NULL, misconceptions TEXT NOT NULL,
 resource_status VARCHAR(20) NOT NULL DEFAULT 'EMPTY',
 review_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
 content_source VARCHAR(500) NOT NULL, syllabus_version VARCHAR(200) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 CONSTRAINT fk_knowledge_chapter FOREIGN KEY(chapter_id) REFERENCES chapter(id),
 CONSTRAINT ck_knowledge_resource CHECK(resource_status IN ('EMPTY','DEMO','PUBLISHED')),
 CONSTRAINT ck_knowledge_review CHECK(review_status IN ('DRAFT','REVIEWED')),
 CONSTRAINT ck_knowledge_publication CHECK(resource_status <> 'PUBLISHED' OR review_status = 'REVIEWED'),
 UNIQUE KEY uk_knowledge_name(chapter_id,name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE knowledge_relation (
 knowledge_id BIGINT NOT NULL, related_id BIGINT NOT NULL, relation_type VARCHAR(20) NOT NULL,
 PRIMARY KEY(knowledge_id,related_id,relation_type),
 FOREIGN KEY(knowledge_id) REFERENCES knowledge_point(id),
 FOREIGN KEY(related_id) REFERENCES knowledge_point(id),
 CONSTRAINT ck_relation_type CHECK(relation_type IN ('PREREQUISITE','RELATED')),
 CONSTRAINT ck_relation_self CHECK(knowledge_id <> related_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE story (
 id BIGINT PRIMARY KEY, title VARCHAR(200) NOT NULL, scene VARCHAR(200) NOT NULL,
 introduction TEXT NOT NULL, illustration VARCHAR(30) NOT NULL,
 resource_status VARCHAR(20) NOT NULL DEFAULT 'DEMO', review_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
 content_source VARCHAR(500) NOT NULL, syllabus_version VARCHAR(200) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 CONSTRAINT ck_story_resource CHECK(resource_status IN ('EMPTY','DEMO','PUBLISHED')),
 CONSTRAINT ck_story_review CHECK(review_status IN ('DRAFT','REVIEWED')),
 CONSTRAINT ck_story_publication CHECK(resource_status <> 'PUBLISHED' OR review_status='REVIEWED')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE story_knowledge (
 story_id BIGINT NOT NULL, knowledge_id BIGINT NOT NULL,
 PRIMARY KEY(story_id,knowledge_id), INDEX ix_story_knowledge_reverse(knowledge_id,story_id),
 FOREIGN KEY(story_id) REFERENCES story(id), FOREIGN KEY(knowledge_id) REFERENCES knowledge_point(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE question (
 id BIGINT PRIMARY KEY, story_id BIGINT NOT NULL, prompt TEXT NOT NULL, sort_order INT NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 FOREIGN KEY(story_id) REFERENCES story(id), UNIQUE KEY uk_question_order(story_id,sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE question_option (
 id BIGINT PRIMARY KEY, question_id BIGINT NOT NULL, label VARCHAR(5) NOT NULL,
 name VARCHAR(200) NOT NULL, term VARCHAR(100) NOT NULL, plain_meaning TEXT NOT NULL,
 is_correct BOOLEAN NOT NULL, reason TEXT NOT NULL, misconception TEXT NOT NULL, feedback TEXT NOT NULL,
 content_source VARCHAR(500) NOT NULL, syllabus_version VARCHAR(200) NOT NULL,
 review_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 FOREIGN KEY(question_id) REFERENCES question(id), UNIQUE KEY uk_option_label(question_id,label),
 CONSTRAINT ck_option_review CHECK(review_status IN ('DRAFT','REVIEWED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE knowledge_card (
 id BIGINT PRIMARY KEY, knowledge_id BIGINT NOT NULL UNIQUE,
 diagram_type VARCHAR(30) NOT NULL,
 content_source VARCHAR(500) NOT NULL, syllabus_version VARCHAR(200) NOT NULL,
 review_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 FOREIGN KEY(knowledge_id) REFERENCES knowledge_point(id),
 CONSTRAINT ck_card_review CHECK(review_status IN ('DRAFT','REVIEWED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
