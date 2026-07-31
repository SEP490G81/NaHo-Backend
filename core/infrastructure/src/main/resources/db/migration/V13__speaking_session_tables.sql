-- V13: AI Speaking 1-1 Session Persistence Tables

CREATE TABLE speaking_sessions
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    created_time     DATETIME(6)           NOT NULL,
    modified_time    DATETIME(6)           NULL,
    session_code     VARCHAR(36)           NOT NULL,
    user_id          BIGINT                NOT NULL,
    persona_id       BIGINT                NULL,
    topic            VARCHAR(500)          NULL,
    marugoto_level   VARCHAR(30)           NULL,
    formality_level  VARCHAR(20)           NULL,
    duration_seconds INT                   NULL,
    total_turns      INT                   NOT NULL DEFAULT 0,
    asr_confidence   DOUBLE                NULL,
    full_transcript  LONGTEXT              NULL,
    status           VARCHAR(20)           NOT NULL DEFAULT 'IN_PROGRESS',
    started_at       DATETIME(6)           NOT NULL,
    ended_at         DATETIME(6)           NULL,
    CONSTRAINT pk_speaking_sessions PRIMARY KEY (id),
    CONSTRAINT uq_speaking_sessions_code UNIQUE (session_code)
);

CREATE TABLE speaking_session_assessments
(
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    created_time            DATETIME(6)           NOT NULL,
    modified_time           DATETIME(6)           NULL,
    session_id              BIGINT                NOT NULL,
    overall_score           INT                   NOT NULL,
    jlpt_estimate           VARCHAR(5)            NOT NULL,
    fluency_score           INT                   NOT NULL,
    pronunciation_score     INT                   NOT NULL,
    grammar_score           INT                   NOT NULL,
    vocabulary_score        INT                   NOT NULL,
    interaction_score       INT                   NOT NULL,
    naturalness_score       INT                   NOT NULL,
    coherence_score         INT                   NOT NULL,
    summary                 TEXT                  NOT NULL,
    strengths               JSON                  NOT NULL,
    weaknesses              JSON                  NOT NULL,
    feedback_fluency        TEXT                  NULL,
    feedback_pronunciation  TEXT                  NULL,
    feedback_grammar        TEXT                  NULL,
    feedback_vocabulary     TEXT                  NULL,
    feedback_interaction    TEXT                  NULL,
    feedback_naturalness    TEXT                  NULL,
    feedback_coherence      TEXT                  NULL,
    study_focus_area        VARCHAR(30)           NULL,
    study_recommendation    TEXT                  NULL,
    study_encouragement     TEXT                  NULL,
    CONSTRAINT pk_speaking_session_assessments PRIMARY KEY (id),
    CONSTRAINT uq_speaking_session_assessments_session UNIQUE (session_id)
);

CREATE TABLE speaking_improved_expressions
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    created_time     DATETIME(6)           NOT NULL,
    modified_time    DATETIME(6)           NULL,
    assessment_id    BIGINT                NOT NULL,
    turn_index       INT                   NULL,
    original_text    TEXT                  NOT NULL,
    improved_text    TEXT                  NOT NULL,
    explanation_vi   TEXT                  NULL,
    CONSTRAINT pk_speaking_improved_expressions PRIMARY KEY (id)
);

CREATE TABLE speaking_session_messages
(
    id                      BIGINT AUTO_INCREMENT NOT NULL,
    created_time            DATETIME(6)           NOT NULL,
    modified_time           DATETIME(6)           NULL,
    session_id              BIGINT                NOT NULL,
    turn_index              INT                   NOT NULL,
    sender_type             VARCHAR(20)           NOT NULL,
    content                 LONGTEXT              NOT NULL,
    corrected_text          TEXT                  NULL,
    correction_explanation  TEXT                  NULL,
    grammar_note            TEXT                  NULL,
    hint_for_learner        TEXT                  NULL,
    pronunciation_score     DOUBLE                NULL,
    CONSTRAINT pk_speaking_session_messages PRIMARY KEY (id)
);

ALTER TABLE speaking_session_assessments
    ADD CONSTRAINT fk_ssa_session
        FOREIGN KEY (session_id) REFERENCES speaking_sessions (id);

ALTER TABLE speaking_improved_expressions
    ADD CONSTRAINT fk_sie_assessment
        FOREIGN KEY (assessment_id) REFERENCES speaking_session_assessments (id);

ALTER TABLE speaking_session_messages
    ADD CONSTRAINT fk_ssm_session
        FOREIGN KEY (session_id) REFERENCES speaking_sessions (id);

CREATE INDEX idx_speaking_sessions_user ON speaking_sessions (user_id);
CREATE INDEX idx_speaking_sessions_status ON speaking_sessions (status);
CREATE INDEX idx_speaking_session_messages_session ON speaking_session_messages (session_id, turn_index);
