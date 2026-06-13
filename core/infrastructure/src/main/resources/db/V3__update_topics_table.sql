ALTER TABLE topics
    CHANGE name japanese_name VARCHAR(255) NULL,
    CHANGE `description` japanese_description VARCHAR(255) NULL,
    ADD COLUMN japanese_name_tokens JSON NULL,
    ADD COLUMN japanese_description_tokens JSON NULL,
    MODIFY COLUMN order_index DOUBLE NULL,
    ADD COLUMN user_id BIGINT NULL;

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE questions
    ADD COLUMN status VARCHAR(50) DEFAULT 'DRAFT';
