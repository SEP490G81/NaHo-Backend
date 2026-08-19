CREATE TABLE ai_feedbacks
(
    id                         BIGINT AUTO_INCREMENT NOT NULL,
    created_time               datetime(6)           NOT NULL,
    modified_time              datetime(6)           NULL,
    grammar_score              DOUBLE                NOT NULL,
    vocabulary_score           DOUBLE                NOT NULL,
    naturalness_score          DOUBLE                NOT NULL,
    content_relevant_score     DOUBLE                NOT NULL,
    average_score              DOUBLE                NOT NULL,
    suggest_japanese_answer    TEXT                  NOT NULL,
    suggest_answer_translation TEXT                  NOT NULL,
    CONSTRAINT pk_ai_feedbacks PRIMARY KEY (id)
);

CREATE TABLE answer_histories
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created_time         datetime(6)           NOT NULL,
    modified_time        datetime(6)           NULL,
    user_id              BIGINT                NOT NULL,
    speaking_question_id BIGINT                NOT NULL,
    speech_assessment_id BIGINT                NOT NULL,
    ai_feedback_id       BIGINT                NOT NULL,
    audio_file_id        BIGINT                NOT NULL,
    duration             DOUBLE                NULL,
    overall_score        DOUBLE                NOT NULL,
    CONSTRAINT pk_answer_histories PRIMARY KEY (id)
);

CREATE TABLE auth_providers
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    created_time     datetime(6)           NOT NULL,
    modified_time    datetime(6)           NULL,
    user_id          BIGINT                NOT NULL,
    provider_user_id VARCHAR(255)          NOT NULL,
    provider_name    SMALLINT              NOT NULL,
    avatar_url       VARCHAR(255)          NULL,
    CONSTRAINT pk_auth_providers PRIMARY KEY (id)
);

CREATE TABLE aws_daily_costs
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime(6)           NOT NULL,
    modified_time datetime(6)           NULL,
    record_date   date                  NOT NULL,
    cost_amount   DECIMAL(18, 12)       NOT NULL,
    currency      VARCHAR(10)           NOT NULL,
    CONSTRAINT pk_aws_daily_costs PRIMARY KEY (id)
);

CREATE TABLE azure_daily_costs
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime(6)           NOT NULL,
    modified_time datetime(6)           NULL,
    record_date   date                  NOT NULL,
    cost_amount   DECIMAL(18, 12)       NOT NULL,
    currency      VARCHAR(10)           NOT NULL,
    CONSTRAINT pk_azure_daily_costs PRIMARY KEY (id)
);

CREATE TABLE books
(
    id                            BIGINT AUTO_INCREMENT NOT NULL,
    created_time                  datetime(6)           NOT NULL,
    modified_time                 datetime(6)           NULL,
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
    created_time  datetime(6)           NOT NULL,
    modified_time datetime(6)           NULL,
    chest_type    VARCHAR(255)          NOT NULL,
    `description` TEXT                  NULL,
    min_point     INT                   NOT NULL,
    max_point     INT                   NOT NULL,
    CONSTRAINT pk_chests PRIMARY KEY (id)
);

CREATE TABLE comments
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created_time         datetime(6)           NOT NULL,
    modified_time        datetime(6)           NULL,
    content              TEXT                  NOT NULL,
    user_id              BIGINT                NOT NULL,
    speaking_question_id BIGINT                NOT NULL,
    parent_id            BIGINT                NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

CREATE TABLE conversation_styles
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_time    datetime(6)           NOT NULL,
    modified_time   datetime(6)           NULL,
    `description`   VARCHAR(512)          NULL,
    prompt          TEXT                  NOT NULL,
    formality_level VARCHAR(255)          NOT NULL,
    marugoto_level  VARCHAR(255)          NULL,
    CONSTRAINT pk_conversation_styles PRIMARY KEY (id)
);

CREATE TABLE daily_missions
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime(6)           NOT NULL,
    modified_time datetime(6)           NULL,
    title         VARCHAR(255)          NOT NULL,
    `description` VARCHAR(255)          NULL,
    mission_type  VARCHAR(255)          NOT NULL,
    point         DOUBLE                NOT NULL,
    CONSTRAINT pk_daily_missions PRIMARY KEY (id)
);

CREATE TABLE daily_rewards
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    created_time      datetime(6)           NOT NULL,
    modified_time     datetime(6)           NULL,
    reward_year_month VARCHAR(255)          NOT NULL,
    day_of_month      INT                   NOT NULL,
    chest_id          BIGINT                NOT NULL,
    CONSTRAINT pk_daily_rewards PRIMARY KEY (id)
);

CREATE TABLE files
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    created_time     datetime(6)           NOT NULL,
    modified_time    datetime(6)           NULL,
    object_key       VARCHAR(500)          NOT NULL,
    bucket_name      VARCHAR(255)          NULL,
    original_name    VARCHAR(255)          NOT NULL,
    content_type     VARCHAR(100)          NOT NULL,
    size             BIGINT                NOT NULL,
    checksum         VARCHAR(255)          NULL,
    operation_type   VARCHAR(50)           NULL,
    operation_status VARCHAR(50)           NULL,
    retry_count      INT                   NOT NULL,
    next_retry_at    datetime(6)           NULL,
    comment_id       BIGINT                NULL,
    report_id        BIGINT                NULL,
    CONSTRAINT pk_files PRIMARY KEY (id)
);

CREATE TABLE grammars
(
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    created_time            datetime(6)           NOT NULL,
    modified_time           datetime(6)           NULL,
    reading                 VARCHAR(255)          NULL,
    japanese                VARCHAR(255)          NULL,
    vietnamese_meaning_text TEXT                  NULL,
    english_meaning_text    TEXT                  NULL,
    CONSTRAINT pk_grammars PRIMARY KEY (id)
);

CREATE TABLE leagues
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime(6)           NOT NULL,
    modified_time datetime(6)           NULL,
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
    created_time           datetime(6)           NOT NULL,
    modified_time          datetime(6)           NULL,
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
    created_time                  datetime(6)           NOT NULL,
    modified_time                 datetime(6)           NULL,
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

CREATE TABLE notifications
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime(6)           NOT NULL,
    modified_time datetime(6)           NULL,
    user_id       BIGINT                NOT NULL,
    type          VARCHAR(50)           NOT NULL,
    title         VARCHAR(255)          NOT NULL,
    content       TEXT                  NOT NULL,
    is_read       BIT(1)                NOT NULL,
    target_url    TEXT                  NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE TABLE objectives
(
    id                            BIGINT AUTO_INCREMENT NOT NULL,
    created_time                  datetime(6)           NOT NULL,
    modified_time                 datetime(6)           NULL,
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

CREATE TABLE openai_daily_costs
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime(6)           NOT NULL,
    modified_time datetime(6)           NULL,
    record_date   date                  NOT NULL,
    cost_amount   DECIMAL(12, 6)        NOT NULL,
    currency      VARCHAR(10)           NOT NULL,
    CONSTRAINT pk_openai_daily_costs PRIMARY KEY (id)
);

CREATE TABLE payment_idempotencies
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    created_time           datetime(6)           NOT NULL,
    modified_time          datetime(6)           NULL,
    user_id                BIGINT                NOT NULL,
    idempotency_key        VARCHAR(100)          NOT NULL,
    request_hash           VARCHAR(64)           NOT NULL,
    payment_order_id       BIGINT                NOT NULL,
    retention_expires_time datetime(6)           NOT NULL,
    CONSTRAINT pk_payment_idempotencies PRIMARY KEY (id)
);

CREATE TABLE payment_orders
(
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    created_time            datetime(6)           NOT NULL,
    modified_time           datetime(6)           NULL,
    order_code              VARCHAR(255)          NOT NULL,
    user_id                 BIGINT                NOT NULL,
    subscription_plan_id    BIGINT                NOT NULL,
    amount_amount           DECIMAL               NOT NULL,
    amount_currency         VARCHAR(255)          NOT NULL,
    provider                VARCHAR(255)          NOT NULL,
    status                  VARCHAR(255)          NOT NULL,
    payment_url             TEXT                  NULL,
    provider_transaction_id VARCHAR(255)          NULL,
    expires_time            datetime(6)           NOT NULL,
    paid_time               datetime(6)           NULL,
    CONSTRAINT pk_payment_orders PRIMARY KEY (id)
);

CREATE TABLE payment_transactions
(
    id                        BIGINT AUTO_INCREMENT NOT NULL,
    created_time              datetime(6)           NOT NULL,
    modified_time             datetime(6)           NULL,
    payment_order_id          BIGINT                NOT NULL,
    provider                  VARCHAR(255)          NOT NULL,
    provider_transaction_id   VARCHAR(255)          NOT NULL,
    amount_amount             DECIMAL               NOT NULL,
    amount_currency           VARCHAR(255)          NOT NULL,
    successful                BIT(1)                NOT NULL,
    provider_transaction_time datetime(6)           NULL,
    metadata                  TEXT                  NULL,
    CONSTRAINT pk_payment_transactions PRIMARY KEY (id)
);

CREATE TABLE permissions
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_time    datetime(6)           NOT NULL,
    modified_time   datetime(6)           NULL,
    permission_code VARCHAR(100)          NOT NULL,
    `description`   VARCHAR(255)          NULL,
    CONSTRAINT pk_permissions PRIMARY KEY (id)
);

CREATE TABLE personas
(
    id                              BIGINT AUTO_INCREMENT NOT NULL,
    created_time                    datetime(6)           NOT NULL,
    modified_time                   datetime(6)           NULL,
    name                            VARCHAR(255)          NOT NULL,
    prompt                          TEXT                  NOT NULL,
    avatar_file_id                  BIGINT                NULL,
    suggested_conversation_style_id BIGINT                NOT NULL,
    CONSTRAINT pk_personas PRIMARY KEY (id)
);

CREATE TABLE point_histories
(
    id                    BIGINT AUTO_INCREMENT NOT NULL,
    created_time          datetime(6)           NOT NULL,
    modified_time         datetime(6)           NULL,
    point                 DOUBLE                NOT NULL,
    transaction_type      VARCHAR(255)          NOT NULL,
    transaction_time      datetime(6)           NOT NULL,
    user_id               BIGINT                NOT NULL,
    learning_path_node_id BIGINT                NULL,
    CONSTRAINT pk_point_histories PRIMARY KEY (id)
);

CREATE TABLE quote
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    kanji        VARCHAR(500)          NOT NULL,
    hiragana     VARCHAR(500)          NOT NULL,
    romaji       VARCHAR(500)          NOT NULL,
    translation  TEXT                  NOT NULL,
    kanji_detail TEXT                  NULL,
    CONSTRAINT pk_quote PRIMARY KEY (id)
);

CREATE TABLE reactions
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created_time         datetime(6)           NOT NULL,
    modified_time        datetime(6)           NULL,
    reaction_type        VARCHAR(255)          NOT NULL,
    user_id              BIGINT                NOT NULL,
    comment_id           BIGINT                NOT NULL,
    speaking_question_id BIGINT                NULL,
    CONSTRAINT pk_reactions PRIMARY KEY (id)
);

CREATE TABLE reports
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created_time         datetime(6)           NOT NULL,
    modified_time        datetime(6)           NULL,
    title                VARCHAR(255)          NOT NULL,
    `description`        TEXT                  NOT NULL,
    report_type          VARCHAR(50)           NOT NULL,
    is_resolved          BIT(1)                NULL,
    admin_reply          TEXT                  NULL,
    user_id              BIGINT                NOT NULL,
    speaking_question_id BIGINT                NULL,
    comment_id           BIGINT                NULL,
    CONSTRAINT pk_reports PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime(6)           NOT NULL,
    modified_time datetime(6)           NULL,
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

CREATE TABLE speaking_improved_expressions
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    created_time   datetime(6)           NOT NULL,
    modified_time  datetime(6)           NULL,
    assessment_id  BIGINT                NOT NULL,
    turn_index     INT                   NULL,
    original_text  TEXT                  NOT NULL,
    improved_text  TEXT                  NOT NULL,
    explanation_vi TEXT                  NULL,
    CONSTRAINT pk_speaking_improved_expressions PRIMARY KEY (id)
);

CREATE TABLE speaking_questions
(
    id                              BIGINT AUTO_INCREMENT NOT NULL,
    created_time                    datetime(6)           NOT NULL,
    modified_time                   datetime(6)           NULL,
    japanese_name                   TEXT                  NOT NULL,
    japanese_name_markup            TEXT                  NOT NULL,
    vietnamese_name                 TEXT                  NULL,
    `description`                   TEXT                  NULL,
    description_markup              TEXT                  NULL,
    japanese_sample_answer          TEXT                  NULL,
    japanese_sample_answer_markup   TEXT                  NULL,
    vietnamese_sample_answer        TEXT                  NULL,
    english_sample_answer           TEXT                  NULL,
    status                          VARCHAR(50)           NULL,
    speaking_question_audio_file_id BIGINT                NULL,
    user_id                         BIGINT                NULL,
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

CREATE TABLE speaking_session_assessments
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    created_time           datetime(6)           NOT NULL,
    modified_time          datetime(6)           NULL,
    session_id             BIGINT                NOT NULL,
    overall_score          INT                   NOT NULL,
    jlpt_estimate          VARCHAR(5)            NOT NULL,
    fluency_score          INT                   NOT NULL,
    pronunciation_score    INT                   NOT NULL,
    grammar_score          INT                   NOT NULL,
    vocabulary_score       INT                   NOT NULL,
    interaction_score      INT                   NOT NULL,
    naturalness_score      INT                   NOT NULL,
    coherence_score        INT                   NOT NULL,
    summary                TEXT                  NOT NULL,
    strengths              JSON                  NOT NULL,
    weaknesses             JSON                  NOT NULL,
    feedback_fluency       TEXT                  NULL,
    feedback_pronunciation TEXT                  NULL,
    feedback_grammar       TEXT                  NULL,
    feedback_vocabulary    TEXT                  NULL,
    feedback_interaction   TEXT                  NULL,
    feedback_naturalness   TEXT                  NULL,
    feedback_coherence     TEXT                  NULL,
    study_focus_area       VARCHAR(30)           NULL,
    study_recommendation   TEXT                  NULL,
    study_encouragement    TEXT                  NULL,
    CONSTRAINT pk_speaking_session_assessments PRIMARY KEY (id)
);

CREATE TABLE speaking_session_messages
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    created_time           datetime(6)           NOT NULL,
    modified_time          datetime(6)           NULL,
    session_id             BIGINT                NOT NULL,
    turn_index             INT                   NOT NULL,
    sender_type            VARCHAR(20)           NOT NULL,
    message_type           VARCHAR(20)           NULL,
    content                LONGTEXT              NOT NULL,
    content_translation    LONGTEXT              NOT NULL,
    corrected_text         TEXT                  NULL,
    correction_explanation TEXT                  NULL,
    grammar_note           TEXT                  NULL,
    hint_for_learner       TEXT                  NULL,
    pronunciation_score    DOUBLE                NULL,
    audio_file_id          BIGINT                NULL,
    CONSTRAINT pk_speaking_session_messages PRIMARY KEY (id)
);

CREATE TABLE speaking_sessions
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    created_time     datetime(6)           NOT NULL,
    modified_time    datetime(6)           NULL,
    session_code     VARCHAR(36)           NOT NULL,
    user_id          BIGINT                NOT NULL,
    persona_id       BIGINT                NULL,
    topic            VARCHAR(500)          NULL,
    voice_name       VARCHAR(100)          NULL,
    marugoto_level   VARCHAR(30)           NULL,
    formality_level  VARCHAR(20)           NULL,
    duration_seconds INT                   NULL,
    total_turns      INT                   NOT NULL,
    asr_confidence   DOUBLE                NULL,
    full_transcript  LONGTEXT              NULL,
    status           VARCHAR(20)           NOT NULL,
    started_at       datetime(6)           NOT NULL,
    ended_at         datetime(6)           NULL,
    CONSTRAINT pk_speaking_sessions PRIMARY KEY (id)
);

CREATE TABLE speech_assessments
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    created_time        datetime(6)           NOT NULL,
    modified_time       datetime(6)           NULL,
    transcript_text     TEXT                  NOT NULL,
    accuracy_score      DOUBLE                NOT NULL,
    fluency_score       DOUBLE                NOT NULL,
    completeness_score  DOUBLE                NOT NULL,
    pronunciation_score DOUBLE                NOT NULL,
    average_score       DOUBLE                NOT NULL,
    CONSTRAINT pk_speech_assessments PRIMARY KEY (id)
);

CREATE TABLE subscription_plans
(
    id                                       BIGINT AUTO_INCREMENT NOT NULL,
    created_time                             datetime(6)           NOT NULL,
    modified_time                            datetime(6)           NULL,
    code                                     VARCHAR(255)          NOT NULL,
    `description`                            TEXT                  NULL,
    tier                                     VARCHAR(255)          NOT NULL,
    price_amount                             DECIMAL               NOT NULL,
    price_currency                           VARCHAR(255)          NOT NULL,
    duration_days                            INT                   NULL,
    daily_speaking_question_evaluation_limit INT                   NOT NULL,
    max_speaking_question_recording_seconds  INT                   NOT NULL,
    max_turns_per_ai_session                 INT                   NOT NULL,
    daily_ai_session_start_limit             INT                   NOT NULL,
    max_ai_turn_speaking_seconds             INT                   NOT NULL,
    max_in_progress_session_count            INT                   NOT NULL,
    sample_answer_enabled                    BIT(1)                NOT NULL,
    status                                   VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_subscription_plans PRIMARY KEY (id)
);

CREATE TABLE topics
(
    id                            BIGINT AUTO_INCREMENT NOT NULL,
    created_time                  datetime(6)           NOT NULL,
    modified_time                 datetime(6)           NULL,
    japanese_name                 VARCHAR(255)          NULL,
    japanese_description          VARCHAR(255)          NULL,
    vietnamese_description        TEXT                  NULL,
    english_description           TEXT                  NULL,
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

CREATE TABLE used_vocabularies_and_grammars
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    created_time   datetime(6)           NOT NULL,
    modified_time  datetime(6)           NULL,
    ai_feedback_id BIGINT                NOT NULL,
    expression     TEXT                  NOT NULL,
    category       VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_used_vocabularies_and_grammars PRIMARY KEY (id)
);

CREATE TABLE user_answer_errors
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    created_time   datetime(6)           NOT NULL,
    modified_time  datetime(6)           NULL,
    ai_feedback_id BIGINT                NOT NULL,
    incorrect      TEXT                  NOT NULL,
    correction     TEXT                  NOT NULL,
    CONSTRAINT pk_user_answer_errors PRIMARY KEY (id)
);

CREATE TABLE user_daily_ai_usages
(
    id                        BIGINT AUTO_INCREMENT NOT NULL,
    created_time              datetime(6)           NOT NULL,
    modified_time             datetime(6)           NULL,
    usage_date                date                  NOT NULL,
    speaking_evaluation_count INT                   NOT NULL,
    ai_session_start_count    INT                   NOT NULL,
    user_id                   BIGINT                NOT NULL,
    CONSTRAINT pk_user_daily_ai_usages PRIMARY KEY (id)
);

CREATE TABLE user_daily_attendances
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_time    datetime(6)           NOT NULL,
    modified_time   datetime(6)           NULL,
    attendance_date date                  NOT NULL,
    earned_point    INT                   NOT NULL,
    user_id         BIGINT                NOT NULL,
    daily_reward_id BIGINT                NOT NULL,
    CONSTRAINT pk_user_daily_attendances PRIMARY KEY (id)
);

CREATE TABLE user_daily_missions
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    created_time     datetime(6)           NOT NULL,
    modified_time    datetime(6)           NULL,
    status           VARCHAR(255)          NULL,
    started_date     date                  NULL,
    completed_date   date                  NULL,
    earned_date      date                  NULL,
    daily_mission_id BIGINT                NOT NULL,
    user_id          BIGINT                NOT NULL,
    CONSTRAINT pk_user_daily_missions PRIMARY KEY (id)
);

CREATE TABLE user_learning_progresses
(
    id                         BIGINT AUTO_INCREMENT NOT NULL,
    created_time               datetime(6)           NOT NULL,
    modified_time              datetime(6)           NULL,
    farthest_available_node_id BIGINT                NOT NULL,
    last_learning_node_id      BIGINT                NULL,
    last_learning_at           datetime(6)           NULL,
    total_point                DOUBLE                NOT NULL,
    current_streak             INT                   NULL,
    longest_streak             INT                   NULL,
    CONSTRAINT pk_user_learning_progresses PRIMARY KEY (id)
);

CREATE TABLE user_node_progresses
(
    id                    BIGINT AUTO_INCREMENT NOT NULL,
    created_time          datetime(6)           NOT NULL,
    modified_time         datetime(6)           NULL,
    best_score            DOUBLE                NULL,
    current_score         DOUBLE                NULL,
    attempt_count         INT                   NULL,
    last_completed_at     datetime(6)           NULL,
    status                VARCHAR(255)          NULL,
    learning_path_node_id BIGINT                NOT NULL,
    user_id               BIGINT                NOT NULL,
    CONSTRAINT pk_user_node_progresses PRIMARY KEY (id)
);

CREATE TABLE user_sessions
(
    id                       BIGINT AUTO_INCREMENT NOT NULL,
    user_id                  BIGINT                NOT NULL,
    hash_refresh_token       VARCHAR(512)          NOT NULL,
    device_id                VARCHAR(100)          NULL,
    user_agent               TEXT                  NULL,
    ip_address               VARCHAR(45)           NULL,
    issued_at                datetime(6)           NOT NULL,
    refresh_token_expires_at datetime(6)           NOT NULL,
    access_token_expires_at  datetime(6)           NOT NULL,
    last_used_at             datetime(6)           NULL,
    revoked_at               datetime(6)           NULL,
    revoked_reason           VARCHAR(50)           NULL,
    CONSTRAINT pk_user_sessions PRIMARY KEY (id)
);

CREATE TABLE user_subscriptions
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created_time         datetime(6)           NOT NULL,
    modified_time        datetime(6)           NULL,
    user_id              BIGINT                NOT NULL,
    subscription_plan_id BIGINT                NOT NULL,
    payment_order_id     BIGINT                NULL,
    status               VARCHAR(255)          NOT NULL,
    start_time           datetime(6)           NOT NULL,
    end_time             datetime(6)           NOT NULL,
    CONSTRAINT pk_user_subscriptions PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                         BIGINT AUTO_INCREMENT NOT NULL,
    created_time               datetime(6)           NOT NULL,
    modified_time              datetime(6)           NULL,
    username                   VARCHAR(36)           NULL,
    email                      VARCHAR(255)          NOT NULL,
    hash_password              VARCHAR(255)          NULL,
    full_name                  VARCHAR(255)          NOT NULL,
    is_email_verified          BIT(1)                NOT NULL,
    failed_login_attempt_count INT                   NOT NULL,
    locked_until               datetime(6)           NULL,
    gender                     VARCHAR(10)           NULL,
    dob                        date                  NULL,
    status                     VARCHAR(20)           NOT NULL,
    role_id                    BIGINT                NULL,
    user_learning_progress_id  BIGINT                NULL,
    avatar_file_id             BIGINT                NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE vocabularies
(
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    created_time            datetime(6)           NOT NULL,
    modified_time           datetime(6)           NULL,
    reading                 VARCHAR(255)          NULL,
    japanese                VARCHAR(255)          NULL,
    vietnamese_meaning_text VARCHAR(255)          NULL,
    english_meaning_text    VARCHAR(255)          NULL,
    CONSTRAINT pk_vocabularies PRIMARY KEY (id)
);

CREATE TABLE vocabulary_questions
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_time  datetime(6)           NOT NULL,
    modified_time datetime(6)           NULL,
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
    created_time         datetime(6)           NOT NULL,
    modified_time        datetime(6)           NULL,
    word                 VARCHAR(255)          NOT NULL,
    accuracy_score       DOUBLE                NOT NULL,
    error_type           VARCHAR(255)          NOT NULL,
    word_markup          TEXT                  NULL,
    speech_assessment_id BIGINT                NOT NULL,
    CONSTRAINT pk_word_assessments PRIMARY KEY (id)
);

ALTER TABLE answer_histories
    ADD CONSTRAINT uc_answer_histories_ai_feedback UNIQUE (ai_feedback_id);

ALTER TABLE answer_histories
    ADD CONSTRAINT uc_answer_histories_audio_file UNIQUE (audio_file_id);

ALTER TABLE answer_histories
    ADD CONSTRAINT uc_answer_histories_speech_assessment UNIQUE (speech_assessment_id);

ALTER TABLE aws_daily_costs
    ADD CONSTRAINT uc_aws_daily_costs_record_date UNIQUE (record_date);

ALTER TABLE azure_daily_costs
    ADD CONSTRAINT uc_azure_daily_costs_record_date UNIQUE (record_date);

ALTER TABLE books
    ADD CONSTRAINT uc_books_cover_image_file UNIQUE (cover_image_file_id);

ALTER TABLE files
    ADD CONSTRAINT uc_files_object_key UNIQUE (object_key);

ALTER TABLE leagues
    ADD CONSTRAINT uc_leagues_icon_file UNIQUE (icon_file_id);

ALTER TABLE learning_path_nodes
    ADD CONSTRAINT uc_learning_path_nodes_global_order_index UNIQUE (global_order_index);

ALTER TABLE learning_path_nodes
    ADD CONSTRAINT uc_learning_path_nodes_speaking_question UNIQUE (speaking_question_id);

ALTER TABLE learning_path_nodes
    ADD CONSTRAINT uc_learning_path_nodes_vocabulary_question UNIQUE (vocabulary_question_id);

ALTER TABLE payment_orders
    ADD CONSTRAINT uc_payment_orders_order_code UNIQUE (order_code);

ALTER TABLE permissions
    ADD CONSTRAINT uc_permissions_permission_code UNIQUE (permission_code);

ALTER TABLE personas
    ADD CONSTRAINT uc_personas_avatar_file UNIQUE (avatar_file_id);

ALTER TABLE personas
    ADD CONSTRAINT uc_personas_name UNIQUE (name);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_role_name UNIQUE (role_name);

ALTER TABLE speaking_questions
    ADD CONSTRAINT uc_speaking_questions_speaking_question_audio_file UNIQUE (speaking_question_audio_file_id);

ALTER TABLE speaking_session_assessments
    ADD CONSTRAINT uc_speaking_session_assessments_session UNIQUE (session_id);

ALTER TABLE speaking_session_messages
    ADD CONSTRAINT uc_speaking_session_messages_audio_file UNIQUE (audio_file_id);

ALTER TABLE speaking_sessions
    ADD CONSTRAINT uc_speaking_sessions_session_code UNIQUE (session_code);

ALTER TABLE subscription_plans
    ADD CONSTRAINT uc_subscription_plans_code UNIQUE (code);

ALTER TABLE topics
    ADD CONSTRAINT uc_topics_cover_image_file UNIQUE (cover_image_file_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_avatar_file UNIQUE (avatar_file_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_user_learning_progress UNIQUE (user_learning_progress_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE openai_daily_costs
    ADD CONSTRAINT uk_openai_daily_costs_date UNIQUE (record_date);

ALTER TABLE payment_idempotencies
    ADD CONSTRAINT uk_payment_idempotency_user_key UNIQUE (user_id, idempotency_key);

ALTER TABLE answer_histories
    ADD CONSTRAINT FK_ANSWER_HISTORIES_ON_AI_FEEDBACK FOREIGN KEY (ai_feedback_id) REFERENCES ai_feedbacks (id);

ALTER TABLE answer_histories
    ADD CONSTRAINT FK_ANSWER_HISTORIES_ON_AUDIO_FILE FOREIGN KEY (audio_file_id) REFERENCES files (id);

ALTER TABLE answer_histories
    ADD CONSTRAINT FK_ANSWER_HISTORIES_ON_SPEAKING_QUESTION FOREIGN KEY (speaking_question_id) REFERENCES speaking_questions (id);

ALTER TABLE answer_histories
    ADD CONSTRAINT FK_ANSWER_HISTORIES_ON_SPEECH_ASSESSMENT FOREIGN KEY (speech_assessment_id) REFERENCES speech_assessments (id);

ALTER TABLE answer_histories
    ADD CONSTRAINT FK_ANSWER_HISTORIES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE auth_providers
    ADD CONSTRAINT FK_AUTH_PROVIDERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE books
    ADD CONSTRAINT FK_BOOKS_ON_COVER_IMAGE_FILE FOREIGN KEY (cover_image_file_id) REFERENCES files (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_PARENT FOREIGN KEY (parent_id) REFERENCES comments (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_SPEAKING_QUESTION FOREIGN KEY (speaking_question_id) REFERENCES speaking_questions (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE daily_rewards
    ADD CONSTRAINT FK_DAILY_REWARDS_ON_CHEST FOREIGN KEY (chest_id) REFERENCES chests (id);

ALTER TABLE files
    ADD CONSTRAINT FK_FILES_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comments (id);

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

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE objectives
    ADD CONSTRAINT FK_OBJECTIVES_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lessons (id);

ALTER TABLE payment_orders
    ADD CONSTRAINT FK_PAYMENT_ORDERS_ON_SUBSCRIPTION_PLAN FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans (id);

ALTER TABLE payment_transactions
    ADD CONSTRAINT FK_PAYMENT_TRANSACTIONS_ON_PAYMENT_ORDER FOREIGN KEY (payment_order_id) REFERENCES payment_orders (id);

ALTER TABLE personas
    ADD CONSTRAINT FK_PERSONAS_ON_AVATAR_FILE FOREIGN KEY (avatar_file_id) REFERENCES files (id);

ALTER TABLE personas
    ADD CONSTRAINT FK_PERSONAS_ON_SUGGESTED_CONVERSATION_STYLE FOREIGN KEY (suggested_conversation_style_id) REFERENCES conversation_styles (id);

ALTER TABLE point_histories
    ADD CONSTRAINT FK_POINT_HISTORIES_ON_LEARNING_PATH_NODE FOREIGN KEY (learning_path_node_id) REFERENCES learning_path_nodes (id);

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

ALTER TABLE speaking_improved_expressions
    ADD CONSTRAINT FK_SPEAKING_IMPROVED_EXPRESSIONS_ON_ASSESSMENT FOREIGN KEY (assessment_id) REFERENCES speaking_session_assessments (id);

ALTER TABLE speaking_questions
    ADD CONSTRAINT FK_SPEAKING_QUESTIONS_ON_SPEAKING_QUESTION_AUDIO_FILE FOREIGN KEY (speaking_question_audio_file_id) REFERENCES files (id);

ALTER TABLE speaking_questions
    ADD CONSTRAINT FK_SPEAKING_QUESTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE speaking_session_assessments
    ADD CONSTRAINT FK_SPEAKING_SESSION_ASSESSMENTS_ON_SESSION FOREIGN KEY (session_id) REFERENCES speaking_sessions (id);

ALTER TABLE speaking_session_messages
    ADD CONSTRAINT FK_SPEAKING_SESSION_MESSAGES_ON_AUDIO_FILE FOREIGN KEY (audio_file_id) REFERENCES files (id);

ALTER TABLE speaking_session_messages
    ADD CONSTRAINT FK_SPEAKING_SESSION_MESSAGES_ON_SESSION FOREIGN KEY (session_id) REFERENCES speaking_sessions (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_BOOK FOREIGN KEY (book_id) REFERENCES books (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_COVER_IMAGE_FILE FOREIGN KEY (cover_image_file_id) REFERENCES files (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE used_vocabularies_and_grammars
    ADD CONSTRAINT FK_USED_VOCABULARIES_AND_GRAMMARS_ON_AI_FEEDBACK FOREIGN KEY (ai_feedback_id) REFERENCES ai_feedbacks (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_AVATAR_FILE FOREIGN KEY (avatar_file_id) REFERENCES files (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_USER_LEARNING_PROGRESS FOREIGN KEY (user_learning_progress_id) REFERENCES user_learning_progresses (id);

ALTER TABLE user_answer_errors
    ADD CONSTRAINT FK_USER_ANSWER_ERRORS_ON_AI_FEEDBACK FOREIGN KEY (ai_feedback_id) REFERENCES ai_feedbacks (id);

ALTER TABLE user_daily_ai_usages
    ADD CONSTRAINT FK_USER_DAILY_AI_USAGES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_daily_attendances
    ADD CONSTRAINT FK_USER_DAILY_ATTENDANCES_ON_DAILY_REWARD FOREIGN KEY (daily_reward_id) REFERENCES daily_rewards (id);

ALTER TABLE user_daily_attendances
    ADD CONSTRAINT FK_USER_DAILY_ATTENDANCES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_daily_missions
    ADD CONSTRAINT FK_USER_DAILY_MISSIONS_ON_DAILY_MISSION FOREIGN KEY (daily_mission_id) REFERENCES daily_missions (id);

ALTER TABLE user_daily_missions
    ADD CONSTRAINT FK_USER_DAILY_MISSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_learning_progresses
    ADD CONSTRAINT FK_USER_LEARNING_PROGRESSES_ON_FARTHEST_AVAILABLE_NODE FOREIGN KEY (farthest_available_node_id) REFERENCES learning_path_nodes (id);

ALTER TABLE user_learning_progresses
    ADD CONSTRAINT FK_USER_LEARNING_PROGRESSES_ON_LAST_LEARNING_NODE FOREIGN KEY (last_learning_node_id) REFERENCES learning_path_nodes (id);

ALTER TABLE user_node_progresses
    ADD CONSTRAINT FK_USER_NODE_PROGRESSES_ON_LEARNING_PATH_NODE FOREIGN KEY (learning_path_node_id) REFERENCES learning_path_nodes (id);

ALTER TABLE user_node_progresses
    ADD CONSTRAINT FK_USER_NODE_PROGRESSES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_sessions
    ADD CONSTRAINT FK_USER_SESSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_subscriptions
    ADD CONSTRAINT FK_USER_SUBSCRIPTIONS_ON_PAYMENT_ORDER FOREIGN KEY (payment_order_id) REFERENCES payment_orders (id);

ALTER TABLE user_subscriptions
    ADD CONSTRAINT FK_USER_SUBSCRIPTIONS_ON_SUBSCRIPTION_PLAN FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans (id);

ALTER TABLE user_subscriptions
    ADD CONSTRAINT FK_USER_SUBSCRIPTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

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

ALTER TABLE vocabulary_questions_vocabularies
    ADD CONSTRAINT fk_vocquevoc_on_vocabulary_entity FOREIGN KEY (vocabulary_id) REFERENCES vocabularies (id);

ALTER TABLE vocabulary_questions_vocabularies
    ADD CONSTRAINT fk_vocquevoc_on_vocabulary_question_entity FOREIGN KEY (vocabulary_question_id) REFERENCES vocabulary_questions (id);