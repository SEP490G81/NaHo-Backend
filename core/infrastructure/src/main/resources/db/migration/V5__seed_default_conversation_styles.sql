-- Seed default conversation styles
INSERT INTO conversation_styles (id, created_time, modified_time, description, prompt, formality_level)
VALUES (1, NOW(), NULL, 'Neutral/Polite Japanese (ます form)', 'Please respond using polite, standard Japanese (desu/masu form). Avoid casual talk or heavy honorifics unless appropriate.', 'NEUTRAL'),
       (2, NOW(), NULL, 'Informal/Casual Japanese (Kore/Sore/etc)', 'Please respond using friendly, casual Japanese (informal form, dictionary form). Speak like a close friend.', 'INFORMAL'),
       (3, NOW(), NULL, 'Formal Japanese (Keigo)', 'Please respond using very formal Japanese (Keigo/honorific forms). Speak like a formal business associate.', 'FORMAL');
