ALTER TABLE speaking_session_messages
    ADD COLUMN audio_file_id BIGINT NULL;

ALTER TABLE speaking_session_messages
    ADD CONSTRAINT FK_SPEAKING_SESSION_MESSAGES_ON_AUDIO_FILE FOREIGN KEY (audio_file_id) REFERENCES files (id);
