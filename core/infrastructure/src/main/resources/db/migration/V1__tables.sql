CREATE TABLE answer_histories
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created_time         datetime              NOT NULL,
    modified_time        datetime              NULL,
    user_id              BIGINT                NOT NULL,
    speaking_question_id BIGINT                NOT NULL,
    audio_file_id        BIGINT                NOT NULL,
    CONSTRAINT pk_answer_histories PRIMARY KEY (id)
);

CREATE TABLE books
(
    id                            BIGINT AUTO_INCREMENT NOT NULL,
    created_time                  datetime              NOT NULL,
    modified_time                 datetime              NULL,
    title                         VARCHAR(255)          NOT NULL,
    `description`                 TEXT                  NULL,
    jlpt_level                    VARCHAR(255)          NULL,
    cefr_level                    VARCHAR(255)          NULL,
    order_index                   DOUBLE                NOT NULL,
    first_node_global_order_index DOUBLE                NULL,
    last_node_global_order_index  DOUBLE                NULL,
    cover_image_file_id           BIGINT                NULL,
    CONSTRAINT pk_books PRIMARY KEY (id)
);

CREATE TABLE chests
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime              NOT NULL,
    modified_time datetime              NULL,
    title         VARCHAR(255)          NOT NULL,
    `description` TEXT                  NULL,
    point         DOUBLE                NOT NULL,
    CONSTRAINT pk_chests PRIMARY KEY (id)
);

CREATE TABLE comments
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created_time         datetime              NOT NULL,
    modified_time        datetime              NULL,
    content              TEXT                  NOT NULL,
    user_id              BIGINT                NOT NULL,
    speaking_question_id BIGINT                NOT NULL,
    parent_id            BIGINT                NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
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

CREATE TABLE conversation_styles
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_time    datetime              NOT NULL,
    modified_time   datetime              NULL,
    `description`   VARCHAR(512)          NULL,
    prompt          TEXT                  NOT NULL,
    formality_level VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_conversation_styles PRIMARY KEY (id)
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
    comment_id    BIGINT                NULL,
    question_id   BIGINT                NULL,
    report_id     BIGINT                NULL,
    CONSTRAINT pk_files PRIMARY KEY (id)
);

CREATE TABLE grammars
(
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    created_time            datetime              NOT NULL,
    modified_time           datetime              NULL,
    vietnamese_meaning_text TEXT                  NULL,
    english_meaning_text    TEXT                  NULL,
    explanation             MEDIUMTEXT            NULL,
    CONSTRAINT pk_grammars PRIMARY KEY (id)
);

CREATE TABLE leagues
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime              NOT NULL,
    modified_time datetime              NULL,
    name          VARCHAR(255)          NOT NULL,
    `description` TEXT                  NULL,
    min_point     DOUBLE                NOT NULL,
    max_point     DOUBLE                NULL,
    icon_file_id  BIGINT                NOT NULL,
    CONSTRAINT pk_leagues PRIMARY KEY (id)
);

CREATE TABLE learning_path_nodes
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    created_time           datetime              NOT NULL,
    modified_time          datetime              NULL,
    global_order_index     DOUBLE                NOT NULL,
    order_index            DOUBLE                NOT NULL,
    node_type              VARCHAR(255)          NOT NULL,
    objective_id           BIGINT                NOT NULL,
    speaking_question_id   BIGINT                NULL,
    vocabulary_question_id BIGINT                NULL,
    chest_id               BIGINT                NULL,
    CONSTRAINT pk_learning_path_nodes PRIMARY KEY (id)
);

CREATE TABLE lessons
(
    id                            BIGINT AUTO_INCREMENT NOT NULL,
    created_time                  datetime              NOT NULL,
    modified_time                 datetime              NULL,
    japanese_name                 VARCHAR(255)          NULL,
    japanese_description          VARCHAR(255)          NULL,
    japanese_name_markup          TEXT                  NULL,
    japanese_description_markup   TEXT                  NULL,
    status                        VARCHAR(50)           NULL,
    order_index                   DOUBLE                NOT NULL,
    first_node_global_order_index DOUBLE                NULL,
    last_node_global_order_index  DOUBLE                NULL,
    topic_id                      BIGINT                NULL,
    CONSTRAINT pk_lessons PRIMARY KEY (id)
);

CREATE TABLE o_auth_providers
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    created_time     datetime              NOT NULL,
    modified_time    datetime              NULL,
    user_id          BIGINT                NOT NULL,
    provider_user_id VARCHAR(255)          NOT NULL,
    provider_name    SMALLINT              NOT NULL,
    avatar_url       VARCHAR(255)          NULL,
    CONSTRAINT pk_o_auth_providers PRIMARY KEY (id)
);

CREATE TABLE objectives
(
    id                            BIGINT AUTO_INCREMENT NOT NULL,
    created_time                  datetime              NOT NULL,
    modified_time                 datetime              NULL,
    japanese_name                 VARCHAR(255)          NULL,
    japanese_description          VARCHAR(255)          NULL,
    japanese_name_markup          TEXT                  NULL,
    japanese_description_markup   TEXT                  NULL,
    status                        VARCHAR(50)           NULL,
    order_index                   DOUBLE                NOT NULL,
    first_node_global_order_index DOUBLE                NULL,
    last_node_global_order_index  DOUBLE                NULL,
    lesson_id                     BIGINT                NULL,
    CONSTRAINT pk_objectives PRIMARY KEY (id)
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

CREATE TABLE personas
(
    id                              BIGINT AUTO_INCREMENT NOT NULL,
    created_time                    datetime              NOT NULL,
    modified_time                   datetime              NULL,
    name                            VARCHAR(255)          NOT NULL,
    prompt                          TEXT                  NOT NULL,
    avatar_file_id                  BIGINT                NULL,
    suggested_conversation_style_id BIGINT                NOT NULL,
    CONSTRAINT pk_personas PRIMARY KEY (id)
);

CREATE TABLE point_histories
(
    id                    BIGINT AUTO_INCREMENT NOT NULL,
    created_time          datetime              NOT NULL,
    modified_time         datetime              NULL,
    point                 DOUBLE                NOT NULL,
    transaction_type      VARCHAR(255)          NOT NULL,
    transaction_time      datetime              NOT NULL,
    user_id               BIGINT                NOT NULL,
    learning_path_node_id BIGINT                NULL,
    objective_id          BIGINT                NULL,
    lesson_id             BIGINT                NULL,
    topic_id              BIGINT                NULL,
    book_id               BIGINT                NULL,
    CONSTRAINT pk_point_histories PRIMARY KEY (id)
);

CREATE TABLE point_summary
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime              NOT NULL,
    modified_time datetime              NULL,
    total_point   DOUBLE                NOT NULL,
    CONSTRAINT pk_point_summary PRIMARY KEY (id)
);

CREATE TABLE reactions
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created_time         datetime              NOT NULL,
    modified_time        datetime              NULL,
    reaction_type        VARCHAR(255)          NOT NULL,
    user_id              BIGINT                NOT NULL,
    comment_id           BIGINT                NULL,
    speaking_question_id BIGINT                NULL,
    CONSTRAINT pk_reactions PRIMARY KEY (id)
);

CREATE TABLE reports
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created_time         datetime              NOT NULL,
    modified_time        datetime              NULL,
    title                VARCHAR(255)          NOT NULL,
    `description`        TEXT                  NOT NULL,
    report_type          VARCHAR(50)           NOT NULL,
    is_resolved          BIT(1)                NULL,
    user_id              BIGINT                NOT NULL,
    speaking_question_id BIGINT                NULL,
    comment_id           BIGINT                NULL,
    CONSTRAINT pk_reports PRIMARY KEY (id)
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

CREATE TABLE seasons
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime              NOT NULL,
    modified_time datetime              NULL,
    season_no     INT                   NOT NULL,
    start_at      datetime              NOT NULL,
    end_at        datetime              NOT NULL,
    CONSTRAINT pk_seasons PRIMARY KEY (id)
);

CREATE TABLE speaking_questions
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    created_time           datetime              NOT NULL,
    modified_time          datetime              NULL,
    title                  VARCHAR(255)          NOT NULL,
    title_markup           VARCHAR(255)          NOT NULL,
    `description`          TEXT                  NULL,
    description_markup     TEXT                  NULL,
    status                 VARCHAR(50)           NULL,
    question_audio_file_id BIGINT                NULL,
    user_id                BIGINT                NOT NULL,
    CONSTRAINT pk_speaking_questions PRIMARY KEY (id)
);

CREATE TABLE speaking_questions_grammars
(
    grammar_id           BIGINT NOT NULL,
    speaking_question_id BIGINT NOT NULL
);

CREATE TABLE speaking_questions_vocabularies
(
    speaking_question_id BIGINT NOT NULL,
    vocabulary_id        BIGINT NOT NULL
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
    id                            BIGINT AUTO_INCREMENT NOT NULL,
    created_time                  datetime              NOT NULL,
    modified_time                 datetime              NULL,
    japanese_name                 VARCHAR(255)          NULL,
    japanese_description          VARCHAR(255)          NULL,
    japanese_name_markup          TEXT                  NULL,
    japanese_description_markup   TEXT                  NULL,
    status                        VARCHAR(50)           NULL,
    order_index                   DOUBLE                NOT NULL,
    first_node_global_order_index DOUBLE                NULL,
    last_node_global_order_index  DOUBLE                NULL,
    cover_image_file_id           BIGINT                NULL,
    user_id                       BIGINT                NULL,
    book_id                       BIGINT                NULL,
    CONSTRAINT pk_topics PRIMARY KEY (id)
);

CREATE TABLE user_learning_progresses
(
    id                         BIGINT AUTO_INCREMENT NOT NULL,
    created_time               datetime              NOT NULL,
    modified_time              datetime              NULL,
    farthest_available_node_id BIGINT                NOT NULL,
    last_learning_node_id      BIGINT                NULL,
    last_learning_at           datetime              NULL,
    current_streak             INT                   NULL,
    longest_streak             INT                   NULL,
    CONSTRAINT pk_user_learning_progresses PRIMARY KEY (id)
);

CREATE TABLE user_node_progresses
(
    id                    BIGINT AUTO_INCREMENT NOT NULL,
    created_time          datetime              NOT NULL,
    modified_time         datetime              NULL,
    best_score            DOUBLE                NULL,
    current_score         DOUBLE                NULL,
    attempt_count         INT                   NULL,
    completed_at          datetime              NULL,
    status                VARCHAR(255)          NULL,
    learning_path_node_id BIGINT                NOT NULL,
    user_id               BIGINT                NOT NULL,
    CONSTRAINT pk_user_node_progresses PRIMARY KEY (id)
);

CREATE TABLE user_season_points
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime              NOT NULL,
    modified_time datetime              NULL,
    season_point  DOUBLE                NOT NULL,
    user_id       BIGINT                NOT NULL,
    season_id     BIGINT                NOT NULL,
    league_id     BIGINT                NOT NULL,
    CONSTRAINT pk_user_season_points PRIMARY KEY (id)
);

CREATE TABLE user_sessions
(
    id                       BIGINT AUTO_INCREMENT NOT NULL,
    user_id                  BIGINT                NOT NULL,
    hash_refresh_token       VARCHAR(512)          NOT NULL,
    device_id                VARCHAR(100)          NULL,
    user_agent               TEXT                  NULL,
    ip_address               VARCHAR(45)           NULL,
    issued_at                datetime              NOT NULL,
    refresh_token_expires_at datetime              NOT NULL,
    access_token_expires_at  datetime              NOT NULL,
    last_used_at             datetime              NULL,
    revoked_at               datetime              NULL,
    revoked_reason           VARCHAR(50)           NULL,
    CONSTRAINT pk_user_sessions PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                        BIGINT AUTO_INCREMENT NOT NULL,
    created_time              datetime              NOT NULL,
    modified_time             datetime              NULL,
    username                  VARCHAR(36)           NULL,
    email                     VARCHAR(255)          NOT NULL,
    hash_password             VARCHAR(255)          NULL,
    full_name                 VARCHAR(255)          NULL,
    gender                    VARCHAR(10)           NULL,
    dob                       date                  NULL,
    jlpt_level                VARCHAR(2)            NOT NULL,
    status                    VARCHAR(20)           NOT NULL,
    point_summary_id          BIGINT                NULL,
    user_learning_progress_id BIGINT                NULL,
    avatar_file_id            BIGINT                NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL
);

CREATE TABLE vocabularies
(
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    created_time            datetime              NOT NULL,
    modified_time           datetime              NULL,
    reading                 VARCHAR(255)          NULL,
    japanese                VARCHAR(255)          NULL,
    vietnamese_meaning_text VARCHAR(255)          NULL,
    english_meaning_text    VARCHAR(255)          NULL,
    CONSTRAINT pk_vocabularies PRIMARY KEY (id)
);

CREATE TABLE vocabulary_questions
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime              NOT NULL,
    modified_time datetime              NULL,
    CONSTRAINT pk_vocabulary_questions PRIMARY KEY (id)
);

CREATE TABLE vocabulary_questions_vocabularies
(
    vocabulary_id          BIGINT NOT NULL,
    vocabulary_question_id BIGINT NOT NULL
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

ALTER TABLE books
    ADD CONSTRAINT uc_books_cover_image_file UNIQUE (cover_image_file_id);

ALTER TABLE content_assessments
    ADD CONSTRAINT uc_content_assessments_answer_history UNIQUE (answer_history_id);

ALTER TABLE leagues
    ADD CONSTRAINT uc_leagues_icon_file UNIQUE (icon_file_id);

ALTER TABLE learning_path_nodes
    ADD CONSTRAINT uc_learning_path_nodes_chest UNIQUE (chest_id);

ALTER TABLE learning_path_nodes
    ADD CONSTRAINT uc_learning_path_nodes_global_order_index UNIQUE (global_order_index);

ALTER TABLE learning_path_nodes
    ADD CONSTRAINT uc_learning_path_nodes_speaking_question UNIQUE (speaking_question_id);

ALTER TABLE learning_path_nodes
    ADD CONSTRAINT uc_learning_path_nodes_vocabulary_question UNIQUE (vocabulary_question_id);

ALTER TABLE permissions
    ADD CONSTRAINT uc_permissions_permission_code UNIQUE (permission_code);

ALTER TABLE personas
    ADD CONSTRAINT uc_personas_avatar_file UNIQUE (avatar_file_id);

ALTER TABLE personas
    ADD CONSTRAINT uc_personas_name UNIQUE (name);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_role_name UNIQUE (role_name);

ALTER TABLE speaking_questions
    ADD CONSTRAINT uc_speaking_questions_question_audio_file UNIQUE (question_audio_file_id);

ALTER TABLE speech_assessments
    ADD CONSTRAINT uc_speech_assessments_answer_history UNIQUE (answer_history_id);

ALTER TABLE topics
    ADD CONSTRAINT uc_topics_cover_image_file UNIQUE (cover_image_file_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_avatar_file UNIQUE (avatar_file_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_point_summary UNIQUE (point_summary_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_user_learning_progress UNIQUE (user_learning_progress_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE answer_histories
    ADD CONSTRAINT FK_ANSWER_HISTORIES_ON_AUDIO_FILE FOREIGN KEY (audio_file_id) REFERENCES files (id);

ALTER TABLE answer_histories
    ADD CONSTRAINT FK_ANSWER_HISTORIES_ON_SPEAKING_QUESTION FOREIGN KEY (speaking_question_id) REFERENCES speaking_questions (id);

ALTER TABLE answer_histories
    ADD CONSTRAINT FK_ANSWER_HISTORIES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE books
    ADD CONSTRAINT FK_BOOKS_ON_COVER_IMAGE_FILE FOREIGN KEY (cover_image_file_id) REFERENCES files (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_PARENT FOREIGN KEY (parent_id) REFERENCES comments (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_SPEAKING_QUESTION FOREIGN KEY (speaking_question_id) REFERENCES speaking_questions (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE content_assessments
    ADD CONSTRAINT FK_CONTENT_ASSESSMENTS_ON_ANSWER_HISTORY FOREIGN KEY (answer_history_id) REFERENCES answer_histories (id);

ALTER TABLE files
    ADD CONSTRAINT FK_FILES_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comments (id);

ALTER TABLE files
    ADD CONSTRAINT FK_FILES_ON_QUESTION FOREIGN KEY (question_id) REFERENCES speaking_questions (id);

ALTER TABLE files
    ADD CONSTRAINT FK_FILES_ON_REPORT FOREIGN KEY (report_id) REFERENCES reports (id);

ALTER TABLE leagues
    ADD CONSTRAINT FK_LEAGUES_ON_ICON_FILE FOREIGN KEY (icon_file_id) REFERENCES files (id);

ALTER TABLE learning_path_nodes
    ADD CONSTRAINT FK_LEARNING_PATH_NODES_ON_CHEST FOREIGN KEY (chest_id) REFERENCES chests (id);

ALTER TABLE learning_path_nodes
    ADD CONSTRAINT FK_LEARNING_PATH_NODES_ON_OBJECTIVE FOREIGN KEY (objective_id) REFERENCES objectives (id);

ALTER TABLE learning_path_nodes
    ADD CONSTRAINT FK_LEARNING_PATH_NODES_ON_SPEAKING_QUESTION FOREIGN KEY (speaking_question_id) REFERENCES speaking_questions (id);

ALTER TABLE learning_path_nodes
    ADD CONSTRAINT FK_LEARNING_PATH_NODES_ON_VOCABULARY_QUESTION FOREIGN KEY (vocabulary_question_id) REFERENCES vocabulary_questions (id);

ALTER TABLE lessons
    ADD CONSTRAINT FK_LESSONS_ON_TOPIC FOREIGN KEY (topic_id) REFERENCES topics (id);

ALTER TABLE objectives
    ADD CONSTRAINT FK_OBJECTIVES_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lessons (id);

ALTER TABLE o_auth_providers
    ADD CONSTRAINT FK_O_AUTH_PROVIDERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE personas
    ADD CONSTRAINT FK_PERSONAS_ON_AVATAR_FILE FOREIGN KEY (avatar_file_id) REFERENCES files (id);

ALTER TABLE personas
    ADD CONSTRAINT FK_PERSONAS_ON_SUGGESTED_CONVERSATION_STYLE FOREIGN KEY (suggested_conversation_style_id) REFERENCES conversation_styles (id);

ALTER TABLE point_histories
    ADD CONSTRAINT FK_POINT_HISTORIES_ON_BOOK FOREIGN KEY (book_id) REFERENCES books (id);

ALTER TABLE point_histories
    ADD CONSTRAINT FK_POINT_HISTORIES_ON_LEARNING_PATH_NODE FOREIGN KEY (learning_path_node_id) REFERENCES learning_path_nodes (id);

ALTER TABLE point_histories
    ADD CONSTRAINT FK_POINT_HISTORIES_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lessons (id);

ALTER TABLE point_histories
    ADD CONSTRAINT FK_POINT_HISTORIES_ON_OBJECTIVE FOREIGN KEY (objective_id) REFERENCES objectives (id);

ALTER TABLE point_histories
    ADD CONSTRAINT FK_POINT_HISTORIES_ON_TOPIC FOREIGN KEY (topic_id) REFERENCES topics (id);

ALTER TABLE point_histories
    ADD CONSTRAINT FK_POINT_HISTORIES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE reactions
    ADD CONSTRAINT FK_REACTIONS_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comments (id);

ALTER TABLE reactions
    ADD CONSTRAINT FK_REACTIONS_ON_SPEAKING_QUESTION FOREIGN KEY (speaking_question_id) REFERENCES speaking_questions (id);

ALTER TABLE reactions
    ADD CONSTRAINT FK_REACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE reports
    ADD CONSTRAINT FK_REPORTS_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comments (id);

ALTER TABLE reports
    ADD CONSTRAINT FK_REPORTS_ON_SPEAKING_QUESTION FOREIGN KEY (speaking_question_id) REFERENCES speaking_questions (id);

ALTER TABLE reports
    ADD CONSTRAINT FK_REPORTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE speaking_questions
    ADD CONSTRAINT FK_SPEAKING_QUESTIONS_ON_QUESTION_AUDIO_FILE FOREIGN KEY (question_audio_file_id) REFERENCES files (id);

ALTER TABLE speaking_questions
    ADD CONSTRAINT FK_SPEAKING_QUESTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE speech_assessments
    ADD CONSTRAINT FK_SPEECH_ASSESSMENTS_ON_ANSWER_HISTORY FOREIGN KEY (answer_history_id) REFERENCES answer_histories (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_BOOK FOREIGN KEY (book_id) REFERENCES books (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_COVER_IMAGE_FILE FOREIGN KEY (cover_image_file_id) REFERENCES files (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_AVATAR_FILE FOREIGN KEY (avatar_file_id) REFERENCES files (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_POINT_SUMMARY FOREIGN KEY (point_summary_id) REFERENCES point_summary (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_USER_LEARNING_PROGRESS FOREIGN KEY (user_learning_progress_id) REFERENCES user_learning_progresses (id);

ALTER TABLE user_learning_progresses
    ADD CONSTRAINT FK_USER_LEARNING_PROGRESSES_ON_FARTHEST_AVAILABLE_NODE FOREIGN KEY (farthest_available_node_id) REFERENCES learning_path_nodes (id);

ALTER TABLE user_learning_progresses
    ADD CONSTRAINT FK_USER_LEARNING_PROGRESSES_ON_LAST_LEARNING_NODE FOREIGN KEY (last_learning_node_id) REFERENCES learning_path_nodes (id);

ALTER TABLE user_node_progresses
    ADD CONSTRAINT FK_USER_NODE_PROGRESSES_ON_LEARNING_PATH_NODE FOREIGN KEY (learning_path_node_id) REFERENCES learning_path_nodes (id);

ALTER TABLE user_node_progresses
    ADD CONSTRAINT FK_USER_NODE_PROGRESSES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_season_points
    ADD CONSTRAINT FK_USER_SEASON_POINTS_ON_LEAGUE FOREIGN KEY (league_id) REFERENCES leagues (id);

ALTER TABLE user_season_points
    ADD CONSTRAINT FK_USER_SEASON_POINTS_ON_SEASON FOREIGN KEY (season_id) REFERENCES seasons (id);

ALTER TABLE user_season_points
    ADD CONSTRAINT FK_USER_SEASON_POINTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_sessions
    ADD CONSTRAINT FK_USER_SESSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE word_assessments
    ADD CONSTRAINT FK_WORD_ASSESSMENTS_ON_SPEECH_ASSESSMENT FOREIGN KEY (speech_assessment_id) REFERENCES speech_assessments (id);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_permission_entity FOREIGN KEY (permission_id) REFERENCES permissions (id);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_role_entity FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE speaking_questions_grammars
    ADD CONSTRAINT fk_spequegra_on_grammar_entity FOREIGN KEY (grammar_id) REFERENCES grammars (id);

ALTER TABLE speaking_questions_grammars
    ADD CONSTRAINT fk_spequegra_on_speaking_question_entity FOREIGN KEY (speaking_question_id) REFERENCES speaking_questions (id);

ALTER TABLE speaking_questions_vocabularies
    ADD CONSTRAINT fk_spequevoc_on_speaking_question_entity FOREIGN KEY (speaking_question_id) REFERENCES speaking_questions (id);

ALTER TABLE speaking_questions_vocabularies
    ADD CONSTRAINT fk_spequevoc_on_vocabulary_entity FOREIGN KEY (vocabulary_id) REFERENCES vocabularies (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role_entity FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user_entity FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE vocabulary_questions_vocabularies
    ADD CONSTRAINT fk_vocquevoc_on_vocabulary_entity FOREIGN KEY (vocabulary_id) REFERENCES vocabularies (id);

ALTER TABLE vocabulary_questions_vocabularies
    ADD CONSTRAINT fk_vocquevoc_on_vocabulary_question_entity FOREIGN KEY (vocabulary_question_id) REFERENCES vocabulary_questions (id);