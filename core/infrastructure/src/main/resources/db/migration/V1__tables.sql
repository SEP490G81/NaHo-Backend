CREATE TABLE answer_histories
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime              NOT NULL,
    modified_time datetime              NULL,
    user_id       BIGINT                NOT NULL,
    question_id   BIGINT                NOT NULL,
    audio_file_id BIGINT                NOT NULL,
    CONSTRAINT pk_answer_histories PRIMARY KEY (id)
);

CREATE TABLE content_assessments
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    created_time      datetime              NOT NULL,
    modified_time     datetime              NULL,
    vocabulary_score  DOUBLE                NOT NULL,
    grammar_score     DOUBLE                NOT NULL,
    ai_feedback       TEXT                  NOT NULL,
    translation_text  TEXT                  NULL,
    answer_history_id BIGINT                NOT NULL,
    CONSTRAINT pk_content_assessments PRIMARY KEY (id)
);

CREATE TABLE files
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime              NOT NULL,
    modified_time datetime              NULL,
    object_key    VARCHAR(2048)         NOT NULL,
    original_name VARCHAR(255)          NOT NULL,
    content_type  VARCHAR(100)          NOT NULL,
    size          BIGINT                NOT NULL,
    CONSTRAINT pk_files PRIMARY KEY (id)
);

CREATE TABLE japanese_tokenizers
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime              NOT NULL,
    modified_time datetime              NULL,
    japanese_text VARCHAR(255)          NOT NULL,
    reading_text  VARCHAR(255)          NULL,
    CONSTRAINT pk_japanese_tokenizers PRIMARY KEY (id)
);

CREATE TABLE permissions
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_time    datetime              NOT NULL,
    modified_time   datetime              NULL,
    permission_code VARCHAR(100)          NOT NULL,
    `description`   VARCHAR(255)          NULL,
    CONSTRAINT pk_permissions PRIMARY KEY (id)
);

CREATE TABLE phrase_japanese_tokenizers
(
    japanese_tokenizer_id     BIGINT NOT NULL,
    question_sample_phrase_id BIGINT NOT NULL
);

CREATE TABLE question_sample_phrases
(
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    created_time            datetime              NOT NULL,
    modified_time           datetime              NULL,
    vietnamese_meaning_text TEXT                  NULL,
    english_meaning_text    TEXT                  NULL,
    question_id             BIGINT                NOT NULL,
    CONSTRAINT pk_question_sample_phrases PRIMARY KEY (id)
);

CREATE TABLE question_vocabularies
(
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    created_time            datetime              NOT NULL,
    modified_time           datetime              NULL,
    vietnamese_meaning_text VARCHAR(255)          NULL,
    english_meaning_text    VARCHAR(255)          NULL,
    question_id             BIGINT                NOT NULL,
    CONSTRAINT pk_question_vocabularies PRIMARY KEY (id)
);

CREATE TABLE questions
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    created_time           datetime              NOT NULL,
    modified_time          datetime              NULL,
    question_text          VARCHAR(255)          NOT NULL,
    contextual_hint        TEXT                  NULL,
    order_index            INT                   NULL,
    status                 VARCHAR(50)           NULL,
    question_audio_file_id BIGINT                NOT NULL,
    topic_id               BIGINT                NOT NULL,
    CONSTRAINT pk_questions PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime              NOT NULL,
    modified_time datetime              NULL,
    role_name     VARCHAR(20)           NOT NULL,
    `description` VARCHAR(255)          NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE roles_permissions
(
    permission_id BIGINT NOT NULL,
    role_id       BIGINT NOT NULL,
    CONSTRAINT pk_roles_permissions PRIMARY KEY (permission_id, role_id)
);

CREATE TABLE speech_assessments
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    created_time        datetime              NOT NULL,
    modified_time       datetime              NULL,
    transcript_text     TEXT                  NOT NULL,
    accuracy_score      DOUBLE                NOT NULL,
    fluency_score       DOUBLE                NOT NULL,
    completeness_score  DOUBLE                NOT NULL,
    pronunciation_score DOUBLE                NOT NULL,
    answer_history_id   BIGINT                NOT NULL,
    CONSTRAINT pk_speech_assessments PRIMARY KEY (id)
);

CREATE TABLE topics
(
    id                          BIGINT AUTO_INCREMENT NOT NULL,
    created_time                datetime              NOT NULL,
    modified_time               datetime              NULL,
    japanese_name               VARCHAR(255)          NULL,
    japanese_description        VARCHAR(255)          NULL,
    japanese_name_tokens        JSON                  NULL,
    japanese_description_tokens JSON                  NULL,
    status                      VARCHAR(50)           NULL,
    jlpt_level                  VARCHAR(2)            NULL,
    order_index                 DOUBLE                NULL,
    cover_image_file_id         BIGINT                NULL,
    user_id                     BIGINT                NULL,
    CONSTRAINT pk_topics PRIMARY KEY (id)
);

CREATE TABLE user_sessions
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    user_id            BIGINT                NOT NULL,
    hash_refresh_token VARCHAR(512)          NOT NULL,
    device_id          VARCHAR(100)          NULL,
    user_agent         TEXT                  NULL,
    ip_address         VARCHAR(45)           NULL,
    issued_at          datetime              NOT NULL,
    expires_at         datetime              NOT NULL,
    last_used_at       datetime              NULL,
    revoked_at         datetime              NULL,
    revoked_reason     VARCHAR(50)           NULL,
    CONSTRAINT pk_user_sessions PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    created_time       datetime              NOT NULL,
    modified_time      datetime              NULL,
    username           VARCHAR(36)           NULL,
    email              VARCHAR(255)          NOT NULL,
    hash_password      VARCHAR(255)          NULL,
    full_name          VARCHAR(200)          NULL,
    gender             VARCHAR(10)           NULL,
    dob                date                  NULL,
    jlpt_level         VARCHAR(2)            NOT NULL,
    status             VARCHAR(20)           NOT NULL,
    current_streak     INT                   NULL,
    longest_streak     INT                   NULL,
    last_practice_date date                  NULL,
    avatar_url         VARCHAR(2048)         NULL,
    provider_id        VARCHAR(512)          NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL
);

CREATE TABLE vocabulary_japanese_tokenizers
(
    japanese_tokenizer_id  BIGINT NOT NULL,
    question_vocabulary_id BIGINT NOT NULL
);

CREATE TABLE word_assessments
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created_time         datetime              NOT NULL,
    modified_time        datetime              NULL,
    word                 VARCHAR(255)          NOT NULL,
    accuracy_score       DOUBLE                NOT NULL,
    error_type           VARCHAR(255)          NOT NULL,
    speech_assessment_id BIGINT                NOT NULL,
    CONSTRAINT pk_word_assessments PRIMARY KEY (id)
);

ALTER TABLE answer_histories
    ADD CONSTRAINT uc_answer_histories_audio_file UNIQUE (audio_file_id);

ALTER TABLE content_assessments
    ADD CONSTRAINT uc_content_assessments_answer_history UNIQUE (answer_history_id);

ALTER TABLE permissions
    ADD CONSTRAINT uc_permissions_permission_code UNIQUE (permission_code);

ALTER TABLE questions
    ADD CONSTRAINT uc_questions_question_audio_file UNIQUE (question_audio_file_id);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_role_name UNIQUE (role_name);

ALTER TABLE speech_assessments
    ADD CONSTRAINT uc_speech_assessments_answer_history UNIQUE (answer_history_id);

ALTER TABLE topics
    ADD CONSTRAINT uc_topics_cover_image_file UNIQUE (cover_image_file_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_provider UNIQUE (provider_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE answer_histories
    ADD CONSTRAINT FK_ANSWER_HISTORIES_ON_AUDIO_FILE FOREIGN KEY (audio_file_id) REFERENCES files (id);

ALTER TABLE answer_histories
    ADD CONSTRAINT FK_ANSWER_HISTORIES_ON_QUESTION FOREIGN KEY (question_id) REFERENCES questions (id);

ALTER TABLE answer_histories
    ADD CONSTRAINT FK_ANSWER_HISTORIES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE content_assessments
    ADD CONSTRAINT FK_CONTENT_ASSESSMENTS_ON_ANSWER_HISTORY FOREIGN KEY (answer_history_id) REFERENCES answer_histories (id);

ALTER TABLE questions
    ADD CONSTRAINT FK_QUESTIONS_ON_QUESTION_AUDIO_FILE FOREIGN KEY (question_audio_file_id) REFERENCES files (id);

ALTER TABLE questions
    ADD CONSTRAINT FK_QUESTIONS_ON_TOPIC FOREIGN KEY (topic_id) REFERENCES topics (id);

ALTER TABLE question_sample_phrases
    ADD CONSTRAINT FK_QUESTION_SAMPLE_PHRASES_ON_QUESTION FOREIGN KEY (question_id) REFERENCES questions (id);

ALTER TABLE question_vocabularies
    ADD CONSTRAINT FK_QUESTION_VOCABULARIES_ON_QUESTION FOREIGN KEY (question_id) REFERENCES questions (id);

ALTER TABLE speech_assessments
    ADD CONSTRAINT FK_SPEECH_ASSESSMENTS_ON_ANSWER_HISTORY FOREIGN KEY (answer_history_id) REFERENCES answer_histories (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_COVER_IMAGE_FILE FOREIGN KEY (cover_image_file_id) REFERENCES files (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_sessions
    ADD CONSTRAINT FK_USER_SESSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE word_assessments
    ADD CONSTRAINT FK_WORD_ASSESSMENTS_ON_SPEECH_ASSESSMENT FOREIGN KEY (speech_assessment_id) REFERENCES speech_assessments (id);

ALTER TABLE phrase_japanese_tokenizers
    ADD CONSTRAINT fk_phrjaptok_on_japanese_tokenizer_entity FOREIGN KEY (japanese_tokenizer_id) REFERENCES japanese_tokenizers (id);

ALTER TABLE phrase_japanese_tokenizers
    ADD CONSTRAINT fk_phrjaptok_on_question_sample_phrase_entity FOREIGN KEY (question_sample_phrase_id) REFERENCES question_sample_phrases (id);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_permission_entity FOREIGN KEY (permission_id) REFERENCES permissions (id);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_role_entity FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role_entity FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user_entity FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE vocabulary_japanese_tokenizers
    ADD CONSTRAINT fk_vocjaptok_on_japanese_tokenizer_entity FOREIGN KEY (japanese_tokenizer_id) REFERENCES japanese_tokenizers (id);

ALTER TABLE vocabulary_japanese_tokenizers
    ADD CONSTRAINT fk_vocjaptok_on_question_vocabulary_entity FOREIGN KEY (question_vocabulary_id) REFERENCES question_vocabularies (id);