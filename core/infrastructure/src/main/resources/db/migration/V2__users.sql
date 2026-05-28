-- Insert Roles
INSERT INTO roles (created_by,
                   created_time,
                   modified_by,
                   modified_time,
                   role_name,
                   description)
VALUES (0, NOW(), NULL, NULL, 'ADMIN', ''),
       (0, NOW(), NULL, NULL, 'STUDENT', ''),
       (0, NOW(), NULL, NULL, 'TEACHER', '');

-- Insert User
INSERT INTO users (created_by,
                   created_time,
                   username,
                   email,
                   hash_password,
                   account_type,
                   jlpt_level,
                   status)
VALUES (0,
        NOW(),
        'vuongtruc2004',
        'vuongtrucwork2004@gmail.com',
        '{bcrypt}$2a$10$mLvl6v0NmQ4f5cP6360qnueyzPYTyLizOJDyr7gL2DbAnnFzbK0mq',
        'CREDENTIALS',
        'N5',
        'ACTIVE');

-- Assign all roles to vuongtruc2004
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         CROSS JOIN roles r
WHERE u.username = 'vuongtruc2004'
  AND r.role_name IN ('ADMIN', 'STUDENT', 'TEACHER');