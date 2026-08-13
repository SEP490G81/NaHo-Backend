-- =========================================================
-- 1. Insert Roles
-- =========================================================

INSERT INTO roles (created_time,
                   modified_time,
                   role_name,
                   description)
VALUES (NOW(), NULL, 'ADMIN', ''),
       (NOW(), NULL, 'LEARNER', ''),
       (NOW(), NULL, 'CONTENT_MANAGER', '');


-- =========================================================
-- 2. Configure email verification
-- =========================================================

ALTER TABLE users
    MODIFY COLUMN is_email_verified BIT (1) NOT NULL DEFAULT b'0';


-- =========================================================
-- 3. Insert User
-- =========================================================
-- Admin
INSERT INTO users (created_time,
                   username,
                   email,
                   full_name,
                   is_email_verified,
                   failed_login_attempt_count,
                   hash_password,
                   status,
                   gender,
                   role_id)
VALUES (NOW(),
        'vuongtruc2004',
        'vuongtrucwork2004@gmail.com',
        'Nguyễn Vương Trực',
        b'1',
        0,
        '{bcrypt}$2a$10$mLvl6v0NmQ4f5cP6360qnueyzPYTyLizOJDyr7gL2DbAnnFzbK0mq',
        'ACTIVE',
        'MALE',
        (SELECT id
         FROM roles
         WHERE role_name = 'ADMIN'));
-- Leaner
INSERT INTO users (id,
                   created_time,
                   username,
                   email,
                   full_name,
                   is_email_verified,
                   failed_login_attempt_count,
                   hash_password,
                   status,
                   gender,
                   role_id)
VALUES (102,
        NOW(),
        'nguyenkhoa2004',
        'nguyendangkhoa5104@gmail.com',
        'Nguyễn Đăng Khoa',
        b'1',
        0,
        '{bcrypt}$2a$10$mLvl6v0NmQ4f5cP6360qnueyzPYTyLizOJDyr7gL2DbAnnFzbK0mq',
        'ACTIVE',
        'MALE',
        (SELECT id
         FROM roles
         WHERE role_name = 'LEARNER'));