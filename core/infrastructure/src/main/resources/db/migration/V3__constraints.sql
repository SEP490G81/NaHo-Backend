ALTER TABLE reactions
    ADD CONSTRAINT uk_reaction_user_comment
        UNIQUE (user_id, comment_id);

ALTER TABLE reactions
    ADD CONSTRAINT uk_reaction_user_question
        UNIQUE (user_id, question_id);