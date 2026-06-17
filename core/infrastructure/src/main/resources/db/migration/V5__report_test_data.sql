-- Insert a test comment for the comment report
INSERT INTO comments (created_time, modified_time, content, user_id, question_id, parent_id)
SELECT NOW(), NULL, 'Đây là bình luận thử nghiệm.', u.id, q.id, NULL
FROM users u, questions q
WHERE u.username = 'vuongtruc2004'
  AND q.title = 'お名前は何ですか？'
LIMIT 1;

-- Insert a QUESTION report
INSERT INTO reports (created_time, modified_time, title, description, report_type, is_resolved, user_id, question_id, comment_id)
SELECT NOW(), NULL, 'Báo cáo lỗi câu hỏi', 'Nội dung câu hỏi bị sai chính tả.', 'QUESTION', 0, u.id, q.id, NULL
FROM users u, questions q
WHERE u.username = 'vuongtruc2004'
  AND q.title = 'お名前は何ですか？'
LIMIT 1;

-- Insert a COMMENT report
INSERT INTO reports (created_time, modified_time, title, description, report_type, is_resolved, user_id, question_id, comment_id)
SELECT NOW(), NULL, 'Báo cáo bình luận tiêu cực', 'Bình luận này mang tính chất công kích.', 'COMMENT', 0, u.id, NULL, c.id
FROM users u, comments c
WHERE u.username = 'vuongtruc2004'
  AND c.content = 'Đây là bình luận thử nghiệm.'
LIMIT 1;

-- Insert a SYSTEM report
INSERT INTO reports (created_time, modified_time, title, description, report_type, is_resolved, user_id, question_id, comment_id)
SELECT NOW(), NULL, 'Báo cáo lỗi hệ thống', 'Giao diện ứng dụng bị đơ khi bấm ghi âm.', 'SYSTEM', 0, u.id, NULL, NULL
FROM users u
WHERE u.username = 'vuongtruc2004'
LIMIT 1;
