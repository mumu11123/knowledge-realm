CREATE TABLE learning_resource (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 title VARCHAR(200) NOT NULL,
 platform VARCHAR(30) NOT NULL,
 author_name VARCHAR(100) NOT NULL,
 author_url VARCHAR(500) NOT NULL,
 url VARCHAR(500) NOT NULL UNIQUE,
 description VARCHAR(300) NOT NULL,
 content_review_status VARCHAR(30) NOT NULL DEFAULT 'NOT_REVIEWED',
 link_verified_at TIMESTAMP NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT ck_resource_review CHECK(content_review_status IN ('NOT_REVIEWED','REVIEWED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE knowledge_resource (
 resource_id BIGINT NOT NULL,
 knowledge_id BIGINT NOT NULL,
 sort_order INT NOT NULL DEFAULT 0,
 PRIMARY KEY(resource_id,knowledge_id),
 FOREIGN KEY(resource_id) REFERENCES learning_resource(id),
 FOREIGN KEY(knowledge_id) REFERENCES knowledge_point(id),
 INDEX ix_knowledge_resources(knowledge_id,sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Links and titles from the creator's own public Bilibili collection, UID 401399175.
-- External enrichment only: no video copying, content approval, course unlock, or AI corpus ingestion.

INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('顺序表(上) - 定义, 创建, 查找','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1CMQEBnE6o/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),201,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('顺序表(下) - 插入, 删除','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1n9oSBUEcZ/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),201,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('单链表(上) - 定义, 创建(头插法, 尾插法)','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1tJTG6YEgs/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),201,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('单链表(下) - 查找, 插入, 删除','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1T2NZ6REg3/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),201,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('双向链表&循环链表','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV15aaf6MEPN/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),203,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('秒杀系列-前后缀表达式计算','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1vG411Q7oz/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),303,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('秒杀系列-中缀表达式转前后缀表达式','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1aV4y1771G/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),303,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('树 - 定义, 术语, 性质','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1xeV5zYEuZ/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),401,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('二叉树&完全二叉树 - 定义, 性质','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1eQ3RzxEoS/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),401,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('二叉树的存储 - 顺序存储, 链式存储','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1dWbQzfEDd/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),402,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('二叉树的遍历 - 前序，中序，后序，层序','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV12T411J72x/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),402,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('二叉树的构造 - 由遍历序列构造二叉树','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1RtezzTEz9/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),402,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('线索二叉树(上) - 概念, 先序/中序/后序线索化','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1xyWEzBE5b/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),403,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('线索二叉树(下) - 找前驱/后继, 遍历','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1jRsXz9EcF/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),403,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('树、森林与二叉树的转换，树和森林的遍历','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1xBqZBAE12/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),404,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('哈夫曼树和哈夫曼编码','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1qu411F7Zs/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),405,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('图 - 定义, 术语, 性质','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV12QASzrErF/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),501,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('图的存储-邻接矩阵','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV18C4y1A7JC/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),501,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('图的存储-邻接表/邻接表画法','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1Ng4y1Z7yN/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),501,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('图的深度优先遍历/深度优先搜索','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1Qp4y1f7Wg/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),502,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('图的广度优先遍历/广度优先搜索','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1VK411t74h/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),502,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('最小生成树-Prim(普里姆)算法和Kruskal(克鲁斯卡尔)算法','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1wG411z79G/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),503,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('最短路径-Dijkstra(迪杰斯特拉)算法','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1uT4y1p7Jy/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),504,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('最短路径-Floyd(弗洛伊德)算法','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV19k4y1Q7Gj/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),504,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('拓扑排序','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1XV411X7T7/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),505,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('AOE网和关键路径','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1dy421a7S1/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),505,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('顺序查找和平均查找长度ASL','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1gt421a7Cq/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),601,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('折半查找(二分查找)','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV19m411S723/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),601,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('折半查找的判定树和平均查找长度(ASL)','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV14K421478y/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),601,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('二叉搜索树(二叉排序树)(二叉查找树)','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1uK421x7JJ/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),602,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('平衡二叉树(AVL树)','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1tZ421q72h/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),602,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('B树(B-树) - 来由, 定义, 插入, 构建','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1tJ4m1w7yR/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),603,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('B树(B-树) - 删除','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1JU411d7iY/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),603,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('B+树','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1bs421u7pY/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),603,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('散列表(哈希表) - 散列函数, 冲突处理, 平均查找长度(ASL)','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV13NwveLE1D/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),604,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('直接插入排序','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1tf421Q7eh/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),701,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('折半插入排序','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1E1421b7Eb/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),701,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('希尔排序','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1bm42137UZ/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),701,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('冒泡排序','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV181421876R/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),702,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('快速排序','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1y4421Z7hK/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),702,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('简单选择排序','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1kjsuenE8v/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),703,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('堆与堆排序','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1HYtseiEQ8/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),703,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('归并排序','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1em1oYTEFf/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),704,0);
INSERT INTO learning_resource(title,platform,author_name,author_url,url,description,link_verified_at) VALUES('基数排序','BILIBILI','蓝不过海呀','https://space.bilibili.com/401399175','https://www.bilibili.com/video/BV1KrzrYeEDw/','用动画辅助理解相关知识，点击前往作者原视频。','2026-10-27 00:00:00');
INSERT INTO knowledge_resource(resource_id,knowledge_id,sort_order) VALUES(LAST_INSERT_ID(),704,0);
