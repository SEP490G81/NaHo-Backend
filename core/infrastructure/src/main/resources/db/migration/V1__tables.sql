CREATE TABLE permissions
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_by    VARCHAR(255)          NOT NULL,
    created_time  datetime              NOT NULL,
    modified_by   VARCHAR(255)          NULL,
    modified_time datetime              NULL,
    code          VARCHAR(255)          NOT NULL,
    `description` VARCHAR(255)          NULL,
    CONSTRAINT pk_permissions PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_by    VARCHAR(255)          NOT NULL,
    created_time  datetime              NOT NULL,
    modified_by   VARCHAR(255)          NULL,
    modified_time datetime              NULL,
    role_name     VARCHAR(255)          NOT NULL,
    `description` VARCHAR(255)          NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE roles_permissions
(
    permission_id BIGINT NOT NULL,
    role_id       BIGINT NOT NULL,
    CONSTRAINT pk_roles_permissions PRIMARY KEY (permission_id, role_id)
);

CREATE TABLE users
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_by    VARCHAR(255)          NOT NULL,
    created_time  datetime              NOT NULL,
    modified_by   VARCHAR(255)          NULL,
    modified_time datetime              NULL,
    username      VARCHAR(255)          NOT NULL,
    email         VARCHAR(255)          NOT NULL,
    password      VARCHAR(255)          NOT NULL,
    first_name    VARCHAR(255)          NULL,
    last_name     VARCHAR(255)          NULL,
    gender        VARCHAR(255)          NULL,
    dob           date                  NULL,
    avatar_url    VARCHAR(255)          NULL,
    jlpt_level    VARCHAR(255)          NULL,
    status        VARCHAR(255)          NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_users_roles PRIMARY KEY (role_id, user_id)
);

ALTER TABLE permissions
    ADD CONSTRAINT uc_permissions_code UNIQUE (code);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_rolename UNIQUE (role_name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_permission_entity FOREIGN KEY (permission_id) REFERENCES permissions (id);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_role_entity FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role_entity FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user_entity FOREIGN KEY (user_id) REFERENCES users (id);