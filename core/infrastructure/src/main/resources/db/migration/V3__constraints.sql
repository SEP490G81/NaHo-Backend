ALTER TABLE reactions
    ADD CONSTRAINT uk_reaction_user_comment
        UNIQUE (user_id, comment_id);

ALTER TABLE reactions
    ADD CONSTRAINT uk_reaction_user_question
        UNIQUE (user_id, speaking_question_id);

ALTER TABLE user_node_progresses
    ADD CONSTRAINT uk_learning_path_node_user
        UNIQUE (user_id, learning_path_node_id);

ALTER TABLE user_daily_attendances
    ADD CONSTRAINT uk_user_daily_reward
        UNIQUE (user_id, daily_reward_id);

ALTER TABLE user_daily_attendances
    ADD CONSTRAINT uk_user_attendance_date
        UNIQUE (user_id, attendance_date);