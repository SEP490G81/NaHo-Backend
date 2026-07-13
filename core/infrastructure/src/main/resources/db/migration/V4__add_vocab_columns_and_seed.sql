-- Alter vocabularies table to add missing reading and japanese columns
ALTER TABLE vocabularies ADD COLUMN reading VARCHAR(255) NULL;
ALTER TABLE vocabularies ADD COLUMN japanese VARCHAR(255) NULL;

-- Create missing join table vocabulary_questions_vocabularies
CREATE TABLE vocabulary_questions_vocabularies
(
    vocabulary_question_id BIGINT NOT NULL,
    vocabulary_id        BIGINT NOT NULL
);

ALTER TABLE vocabulary_questions_vocabularies
    ADD CONSTRAINT fk_vocquevoc_on_vocabulary_question FOREIGN KEY (vocabulary_question_id) REFERENCES vocabulary_questions (id);

ALTER TABLE vocabulary_questions_vocabularies
    ADD CONSTRAINT fk_vocquevoc_on_vocabulary FOREIGN KEY (vocabulary_id) REFERENCES vocabularies (id);

-- Seed Book
INSERT INTO books (id, created_time, modified_time, title, description, jlpt_level, cefr_level, order_index, cover_image_file_id)
VALUES (1, NOW(), NULL, 'Pre-Intermediate Japanese', 'Pre-Intermediate Japanese Course Book', 'N4', 'A2', 1.0, NULL);

-- Seed Topics
INSERT INTO topics (id, created_time, modified_time, japanese_name, japanese_description, japanese_name_markup, japanese_description_markup, status, order_index, cover_image_file_id, user_id, book_id)
VALUES (1, NOW(), NULL, 'Topic 1: 友だちを外出にさそう', 'Giới thiệu và mời bạn đi chơi', NULL, NULL, 'PUBLISHED', 1.0, NULL, 1, 1),
       (2, NOW(), NULL, 'Topic 2: 住むところをさがす', 'Tìm kiếm nhà ở', NULL, NULL, 'PUBLISHED', 2.0, NULL, 1, 1);

-- Seed Lessons
INSERT INTO lessons (id, created_time, modified_time, japanese_name, japanese_description, japanese_name_markup, japanese_description_markup, status, order_index, topic_id)
VALUES (1, NOW(), NULL, 'Lesson 1', 'Bài 1', NULL, NULL, 'PUBLISHED', 1.0, 1),
       (2, NOW(), NULL, 'Lesson 2', 'Bài 2', NULL, NULL, 'PUBLISHED', 1.0, 2);

-- Seed Objectives
INSERT INTO objectives (id, created_time, modified_time, japanese_name, japanese_description, japanese_name_markup, japanese_description_markup, status, order_index, lesson_id)
VALUES (1, NOW(), NULL, '友だちを外出にさそう／さそいをうける', 'Mời bạn đi chơi / Nhận lời mời', NULL, NULL, 'PUBLISHED', 1.0, 1),
       (2, NOW(), NULL, 'りゆうを言ってさそいをことわる', 'Đưa ra lý do từ chối lời mời', NULL, NULL, 'PUBLISHED', 2.0, 1),
       (3, NOW(), NULL, 'りゆうを言ってやくそくをキャンセルする', 'Đưa ra lý do hủy hẹn', NULL, NULL, 'PUBLISHED', 3.0, 1),
       (4, NOW(), NULL, 'おわびのメールとへんじのメールから、じじつと書いた人の気持ちを読みとる', 'Đọc hiểu email xin lỗi', NULL, NULL, 'PUBLISHED', 4.0, 1),
       (5, NOW(), NULL, '外出の報告のメールから、じじつと書いた人の気持ちを読みとる', 'Đọc hiểu email báo cáo đi chơi', NULL, NULL, 'PUBLISHED', 5.0, 1),
       (6, NOW(), NULL, '住むところをさがすのにだいじなポイントは何か話す', 'Điểm quan trọng khi tìm nhà', NULL, NULL, 'PUBLISHED', 1.0, 2),
       (7, NOW(), NULL, '自分が住んでいるところについて話す', 'Nói về nơi mình đang sống', NULL, NULL, 'PUBLISHED', 2.0, 2),
       (8, NOW(), NULL, 'サイトのきじから、どんな家に住んでいるか、そのりゆうは何か読みとる', 'Đọc hiểu bài viết về nhà ở', NULL, NULL, 'PUBLISHED', 3.0, 2),
       (9, NOW(), NULL, 'サイトのきじから、仕事と住むところについて書いた人の考え方を読みとる', 'Đọc hiểu quan điểm về công việc và chỗ ở', NULL, NULL, 'PUBLISHED', 4.0, 2);

-- Seed Vocabulary Questions
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

-- Seed Learning Path Nodes
INSERT INTO learning_path_nodes (id, created_time, modified_time, global_order_index, order_index, node_type, objective_id, speaking_question_id, vocabulary_question_id, chest_id)
VALUES (1, NOW(), NULL, 1.0, 1.0, 'VOCABULARY_QUESTION', 1, NULL, 1, NULL),
       (2, NOW(), NULL, 2.0, 2.0, 'VOCABULARY_QUESTION', 2, NULL, 2, NULL),
       (3, NOW(), NULL, 3.0, 3.0, 'VOCABULARY_QUESTION', 3, NULL, 3, NULL),
       (4, NOW(), NULL, 4.0, 4.0, 'VOCABULARY_QUESTION', 4, NULL, 4, NULL),
       (5, NOW(), NULL, 5.0, 5.0, 'VOCABULARY_QUESTION', 5, NULL, 5, NULL),
       (6, NOW(), NULL, 6.0, 6.0, 'VOCABULARY_QUESTION', 6, NULL, 6, NULL),
       (7, NOW(), NULL, 7.0, 7.0, 'VOCABULARY_QUESTION', 7, NULL, 7, NULL),
       (8, NOW(), NULL, 8.0, 8.0, 'VOCABULARY_QUESTION', 8, NULL, 8, NULL),
       (9, NOW(), NULL, 9.0, 9.0, 'VOCABULARY_QUESTION', 9, NULL, 9, NULL);

-- Seed one speaking question for testing speaking score API
INSERT INTO speaking_questions (id, created_time, modified_time, title, title_markup, description, description_markup, status, question_audio_file_id, user_id)
VALUES (1, NOW(), NULL, 'General conversation', 'General conversation', 'Hãy tự giới thiệu bản thân', 'Hãy tự giới thiệu bản thân', 'PUBLISHED', NULL, 1);

INSERT INTO learning_path_nodes (id, created_time, modified_time, global_order_index, order_index, node_type, objective_id, speaking_question_id, vocabulary_question_id, chest_id)
VALUES (10, NOW(), NULL, 10.0, 10.0, 'SPEAKING_QUESTION', 1, 1, NULL, NULL);
