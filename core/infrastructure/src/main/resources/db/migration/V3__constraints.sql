ALTER TABLE reactions
    ADD CONSTRAINT uk_reaction_user_comment
        UNIQUE (user_id, comment_id);

ALTER TABLE reactions
    ADD CONSTRAINT uk_reaction_user_question
        UNIQUE (user_id, speaking_question_id);

ALTER TABLE user_node_progresses
    ADD CONSTRAINT uk_learning_path_node_user
        UNIQUE (user_id, learning_path_node_id)