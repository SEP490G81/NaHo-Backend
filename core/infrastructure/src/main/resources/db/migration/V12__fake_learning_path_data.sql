-- ============================================================
-- SEED CHESTS
-- ============================================================
INSERT INTO chests (id, created_time, modified_time, title, description, point)
VALUES
  (1, NOW(), NULL, 'Rương Vàng 1', 'Phần thưởng hoàn thành Objective 1', 25.0),
  (2, NOW(), NULL, 'Rương Vàng 2', 'Phần thưởng hoàn thành Objective 2', 25.0),
  (3, NOW(), NULL, 'Rương Vàng 3', 'Phần thưởng hoàn thành Objective 3', 25.0),
  (4, NOW(), NULL, 'Rương Vàng 4', 'Phần thưởng hoàn thành Objective 4', 25.0),
  (5, NOW(), NULL, 'Rương Vàng 5', 'Phần thưởng hoàn thành Objective 5', 25.0),
  (6, NOW(), NULL, 'Rương Vàng 6', 'Phần thưởng hoàn thành Objective 6', 25.0),
  (7, NOW(), NULL, 'Rương Vàng 7', 'Phần thưởng hoàn thành Objective 7', 25.0),
  (8, NOW(), NULL, 'Rương Vàng 8', 'Phần thưởng hoàn thành Objective 8', 25.0),
  (9, NOW(), NULL, 'Rương Vàng 9', 'Phần thưởng hoàn thành Objective 9', 25.0);

-- ============================================================
-- SEED LEARNING PATH NODES FOR CHESTS
-- ============================================================
INSERT INTO learning_path_nodes (id, created_time, modified_time, global_order_index, order_index, node_type, objective_id, speaking_question_id, vocabulary_question_id, chest_id)
VALUES
  (1001, NOW(), NULL, 1.5, 2.0, 'CHEST', 1, NULL, NULL, 1),
  (1002, NOW(), NULL, 2.5, 2.0, 'CHEST', 2, NULL, NULL, 2),
  (1003, NOW(), NULL, 3.5, 2.0, 'CHEST', 3, NULL, NULL, 3),
  (1004, NOW(), NULL, 4.5, 2.0, 'CHEST', 4, NULL, NULL, 4),
  (1005, NOW(), NULL, 5.5, 2.0, 'CHEST', 5, NULL, NULL, 5),
  (1006, NOW(), NULL, 6.5, 2.0, 'CHEST', 6, NULL, NULL, 6),
  (1007, NOW(), NULL, 7.5, 2.0, 'CHEST', 7, NULL, NULL, 7),
  (1008, NOW(), NULL, 8.5, 2.0, 'CHEST', 8, NULL, NULL, 8),
  (1009, NOW(), NULL, 9.5, 2.0, 'CHEST', 9, NULL, NULL, 9);
