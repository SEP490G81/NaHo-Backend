ALTER TABLE files ADD COLUMN comment_id BIGINT; -- Hoặc INT tùy thuộc kiểu dữ liệu của ID bảng comment
ALTER TABLE files ADD COLUMN question_id BIGINT; -- Hoặc INT tùy theo kiểu ID của bảng questions
ALTER TABLE files ADD COLUMN report_id BIGINT;
/* Nếu ở bước trước bạn chưa thêm question_id hay comment_id thì có thể viết chung vào đây:
ALTER TABLE files ADD COLUMN question_id BIGINT;
ALTER TABLE files ADD COLUMN comment_id BIGINT;
*/