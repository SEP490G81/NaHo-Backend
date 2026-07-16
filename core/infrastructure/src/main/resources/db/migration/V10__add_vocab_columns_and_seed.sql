-- 1. Thêm các câu hỏi từ vựng vào bảng vocabulary_questions
INSERT INTO vocabulary_questions (id, created_time, modified_time)
VALUES (1, NOW(), NULL),
       (2, NOW(), NULL),
       (3, NOW(), NULL),
       (4, NOW(), NULL),
       (5, NOW(), NULL),
       (6, NOW(), NULL),
       (7, NOW(), NULL),
       (8, NOW(), NULL),
       (9, NOW(), NULL);

-- 2. Thêm các node liên kết tương ứng vào learning_path_nodes
INSERT INTO learning_path_nodes (id,
                                 created_time,
                                 modified_time,
                                 global_order_index,
                                 order_index,
                                 node_type,
                                 objective_id,
                                 speaking_question_id,
                                 vocabulary_question_id,
                                 chest_id)
VALUES (40, NOW(), NULL, 40.0, 2.0, 'VOCABULARY_QUESTION', 1, NULL, 1, NULL), -- Objective 1, Node thứ 2
       (41, NOW(), NULL, 41.0, 2.0, 'VOCABULARY_QUESTION', 2, NULL, 2, NULL), -- Objective 2, Node thứ 2
       (42, NOW(), NULL, 42.0, 2.0, 'VOCABULARY_QUESTION', 3, NULL, 3, NULL), -- Objective 3, Node thứ 2
       (43, NOW(), NULL, 43.0, 2.0, 'VOCABULARY_QUESTION', 4, NULL, 4, NULL), -- Objective 4, Node thứ 2
       (44, NOW(), NULL, 44.0, 2.0, 'VOCABULARY_QUESTION', 5, NULL, 5, NULL), -- Objective 5, Node thứ 2
       (45, NOW(), NULL, 45.0, 2.0, 'VOCABULARY_QUESTION', 6, NULL, 6, NULL), -- Objective 6, Node thứ 2
       (46, NOW(), NULL, 46.0, 2.0, 'VOCABULARY_QUESTION', 7, NULL, 7, NULL), -- Objective 7, Node thứ 2
       (47, NOW(), NULL, 47.0, 2.0, 'VOCABULARY_QUESTION', 8, NULL, 8, NULL), -- Objective 8, Node thứ 2
       (48, NOW(), NULL, 48.0, 2.0, 'VOCABULARY_QUESTION', 9, NULL, 9, NULL); -- Objective 9, Node thứ 2
