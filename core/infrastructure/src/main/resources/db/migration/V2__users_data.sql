-- Insert Roles
INSERT INTO roles (created_time,
                   modified_time,
                   role_name,
                   description)
VALUES (NOW(), NULL, 'ADMIN', ''),
       (NOW(), NULL, 'LEARNER', ''),
       (NOW(), NULL, 'CONTENT_MANAGER', '');


ALTER TABLE users
    MODIFY COLUMN is_email_verified BIT(1) NOT NULL DEFAULT b'0';

-- Insert User
INSERT INTO users (created_time,
                   username,
                   email,
                   full_name,
                   is_email_verified,
                   hash_password,
                   jlpt_level,
                   status)
VALUES (NOW(),
        'vuongtruc2004',
        'vuongtrucwork2004@gmail.com',
        'Nguyễn Vương Trực',
        1,
        '{bcrypt}$2a$10$mLvl6v0NmQ4f5cP6360qnueyzPYTyLizOJDyr7gL2DbAnnFzbK0mq',
        'N5',
        'ACTIVE');

-- Assign all roles to vuongtruc2004
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         CROSS JOIN roles r
WHERE u.username = 'vuongtruc2004'
  AND r.role_name IN ('ADMIN', 'LEARNER', 'CONTENT_MANAGER');