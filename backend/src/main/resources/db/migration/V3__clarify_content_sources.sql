-- Preserve already-applied migrations; correct source URLs with a forward-only migration.
SET @list_source = '原创演示；概念参考 OpenDSA https://opendsa.org/OpenDSA/Books/Catalog/html/ListAnalysis.html ；未经过教材/考纲审核';
SET @stack_source = '原创演示；概念参考 OpenDSA https://opendsa.org/OpenDSA/Books/Catalog/html/StackArray.html ；未经过教材/考纲审核';
SET @queue_source = '原创演示；概念参考 OpenDSA https://opendsa.org/OpenDSA/Books/CS3/html/Queue.html ；未经过教材/考纲审核';
UPDATE knowledge_point SET content_source = '原创基础目录整理；未按指定教材或考纲审核' WHERE resource_status = 'EMPTY';
UPDATE knowledge_point SET content_source = @list_source WHERE id = 201;
UPDATE knowledge_point SET content_source = @stack_source WHERE id = 301;
UPDATE knowledge_point SET content_source = @queue_source WHERE id = 302;
UPDATE story SET content_source = CASE id WHEN 1 THEN @list_source WHEN 2 THEN @stack_source ELSE @queue_source END;
UPDATE question_option SET content_source = CASE question_id WHEN 11 THEN @list_source WHEN 21 THEN @stack_source ELSE @queue_source END;
UPDATE knowledge_card SET content_source = CASE knowledge_id WHEN 201 THEN @list_source WHEN 301 THEN @stack_source ELSE @queue_source END;
