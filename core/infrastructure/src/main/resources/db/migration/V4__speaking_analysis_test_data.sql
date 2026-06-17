-- Insert a dummy file for the question audio
INSERT INTO files (created_time, modified_time, object_key, original_name, content_type, size)
VALUES (NOW(), NULL, 'test/audio/q1.wav', 'q1.wav', 'audio/wav', 123456);

-- Insert a test topic (JLPT N5)
INSERT INTO topics (created_time, modified_time, japanese_name, japanese_description, japanese_name_markup, japanese_description_markup, status, jlpt_level, order_index, cover_image_file_id, user_id)
SELECT NOW(), NULL, '自己紹介', 'Giới thiệu bản thân', '自己紹介', 'Giới thiệu bản thân', 'ACTIVE', 'N5', 1.0, f.id, u.id
FROM files f, users u
WHERE f.object_key = 'test/audio/q1.wav'
  AND u.username = 'vuongtruc2004'
LIMIT 1;

-- Insert a test question under the topic
INSERT INTO questions (created_time, modified_time, title, title_markup, description, description_markup, order_index, status, question_audio_file_id, topic_id, user_id)
SELECT NOW(), NULL, 'お名前は何ですか？', 'おなまえはなんですか？', 'Tên của bạn là gì?', 'Tên của bạn là gì?', 1.0, 'PUBLISHED', f.id, t.id, u.id
FROM files f, topics t, users u
WHERE f.object_key = 'test/audio/q1.wav'
  AND t.japanese_name = '自己紹介'
  AND u.username = 'vuongtruc2004'
LIMIT 1;

