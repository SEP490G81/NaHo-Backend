-- Duplicate data
# -- Seed Vocabulary Questions
# INSERT INTO vocabulary_questions (id, created_time, modified_time)
# VALUES (1, NOW(), NULL),
#        (2, NOW(), NULL),
#        (3, NOW(), NULL),
#        (4, NOW(), NULL),
#        (5, NOW(), NULL),
#        (6, NOW(), NULL),
#        (7, NOW(), NULL),
#        (8, NOW(), NULL),
#        (9, NOW(), NULL);
#
# -- Seed Learning Path Nodes
# INSERT INTO learning_path_nodes (id, created_time, modified_time, global_order_index, order_index, node_type,
#                                  objective_id, speaking_question_id, vocabulary_question_id, chest_id)
# VALUES (1, NOW(), NULL, 1.0, 1.0, 'VOCABULARY_QUESTION', 1, NULL, 1, NULL),
#        (2, NOW(), NULL, 2.0, 2.0, 'VOCABULARY_QUESTION', 2, NULL, 2, NULL),
#        (3, NOW(), NULL, 3.0, 3.0, 'VOCABULARY_QUESTION', 3, NULL, 3, NULL),
#        (4, NOW(), NULL, 4.0, 4.0, 'VOCABULARY_QUESTION', 4, NULL, 4, NULL),
#        (5, NOW(), NULL, 5.0, 5.0, 'VOCABULARY_QUESTION', 5, NULL, 5, NULL),
#        (6, NOW(), NULL, 6.0, 6.0, 'VOCABULARY_QUESTION', 6, NULL, 6, NULL),
#        (7, NOW(), NULL, 7.0, 7.0, 'VOCABULARY_QUESTION', 7, NULL, 7, NULL),
#        (8, NOW(), NULL, 8.0, 8.0, 'VOCABULARY_QUESTION', 8, NULL, 8, NULL),
#        (9, NOW(), NULL, 9.0, 9.0, 'VOCABULARY_QUESTION', 9, NULL, 9, NULL);
