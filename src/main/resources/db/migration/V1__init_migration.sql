CREATE TABLE channel
(
    id          VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    server_id   VARCHAR(255),
    name        VARCHAR(255),
    description VARCHAR(255),
    is_private  BOOLEAN      NOT NULL,
    is_group    BOOLEAN      NOT NULL,
    CONSTRAINT pk_channel PRIMARY KEY (id)
);

CREATE TABLE channel_members
(
    created_at           TIMESTAMP WITHOUT TIME ZONE,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255),
    joined_date          TIMESTAMP WITHOUT TIME ZONE,
    last_read_message_id VARCHAR(255),
    channel_id           VARCHAR(255) NOT NULL,
    user_id              VARCHAR(255) NOT NULL,
    CONSTRAINT pk_channelmembers PRIMARY KEY (channel_id, user_id)
);

CREATE TABLE channel_read_state
(
    created_at           TIMESTAMP WITHOUT TIME ZONE,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255),
    user_id              VARCHAR(255),
    channel_id           VARCHAR(255),
    last_read_message_id VARCHAR(255),
    last_read_at         TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE friendships
(
    id                 VARCHAR(255) NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255),
    requester_id       VARCHAR(255) NOT NULL,
    addressee_id       VARCHAR(255) NOT NULL,
    friend_ship_status VARCHAR(255) NOT NULL,
    CONSTRAINT pk_friendships PRIMARY KEY (id)
);

CREATE TABLE message
(
    id           VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255),
    channel_id   VARCHAR(255),
    sender_id    VARCHAR(255),
    message_type VARCHAR(255) NOT NULL,
    content      VARCHAR(255) NOT NULL,
    CONSTRAINT pk_message PRIMARY KEY (id)
);

CREATE TABLE message_attachments
(
    id                 VARCHAR(255) NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255),
    message_id         VARCHAR(255),
    file_name          VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_size          BIGINT       NOT NULL,
    mime_type          VARCHAR(255) NOT NULL,
    url                VARCHAR(255) NOT NULL,
    CONSTRAINT pk_messageattachments PRIMARY KEY (id)
);

CREATE TABLE message_reactions
(
    id         VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    message_id VARCHAR(255),
    user_id    VARCHAR(255),
    emoji_id   VARCHAR(255),
    CONSTRAINT pk_messagereactions PRIMARY KEY (id)
);

CREATE TABLE notifications
(
    id                VARCHAR(255) NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    user_id           VARCHAR(255),
    type              VARCHAR(255),
    title             VARCHAR(255) NOT NULL,
    content           VARCHAR(255) NOT NULL,
    read              BOOLEAN      NOT NULL,
    related_entity_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id         VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE server
(
    id          VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    name        VARCHAR(255),
    description VARCHAR(255),
    icon_url    VARCHAR(255),
    is_public   BOOLEAN      NOT NULL,
    max_members INTEGER      NOT NULL,
    CONSTRAINT pk_server PRIMARY KEY (id)
);

CREATE TABLE server_invites
(
    id         VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    server_id  VARCHAR(255),
    code       VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE,
    max_uses   INTEGER      NOT NULL,
    uses       INTEGER      NOT NULL,
    CONSTRAINT pk_serverinvites PRIMARY KEY (id)
);

CREATE TABLE server_members
(
    nickname   VARCHAR(255),
    is_banned  BOOLEAN      NOT NULL,
    ban_reason VARCHAR(255),
    role_id    VARCHAR(255) NOT NULL,
    server_id  VARCHAR(255) NOT NULL,
    user_id    VARCHAR(255) NOT NULL,
    CONSTRAINT pk_server_members PRIMARY KEY (server_id, user_id)
);

CREATE TABLE users
(
    id              VARCHAR(255) NOT NULL,
    username        VARCHAR(255) NOT NULL,
    first_name      VARCHAR(255),
    last_name       VARCHAR(255),
    email           VARCHAR(255) NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    last_seen       TIMESTAMP WITHOUT TIME ZONE,
    is_online       BOOLEAN      NOT NULL,
    last_message_at TIMESTAMP WITHOUT TIME ZONE,
    total_messages  BIGINT,
    servers_joined  BIGINT,
    friends_count   BIGINT,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE channel_read_state
    ADD CONSTRAINT pk_channelreadstate PRIMARY KEY (channel_id, user_id);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);

ALTER TABLE server_invites
    ADD CONSTRAINT uc_serverinvites_code UNIQUE (code);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

CREATE UNIQUE INDEX idx_server_invites_code ON server_invites (code);

CREATE INDEX idx_type_related_entity ON notifications (type, related_entity_id);

ALTER TABLE channel_members
    ADD CONSTRAINT FK_CHANNELMEMBERS_ON_CHANNEL FOREIGN KEY (channel_id) REFERENCES channel (id);

ALTER TABLE channel_members
    ADD CONSTRAINT FK_CHANNELMEMBERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE channel_read_state
    ADD CONSTRAINT FK_CHANNELREADSTATE_ON_CHANNEL FOREIGN KEY (channel_id) REFERENCES channel (id);

ALTER TABLE channel_read_state
    ADD CONSTRAINT FK_CHANNELREADSTATE_ON_LASTREADMESSAGE FOREIGN KEY (last_read_message_id) REFERENCES message (id);

ALTER TABLE channel_read_state
    ADD CONSTRAINT FK_CHANNELREADSTATE_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE channel
    ADD CONSTRAINT FK_CHANNEL_ON_SERVER FOREIGN KEY (server_id) REFERENCES server (id);

ALTER TABLE friendships
    ADD CONSTRAINT FK_FRIENDSHIPS_ON_ADDRESSEE FOREIGN KEY (addressee_id) REFERENCES users (id);

ALTER TABLE friendships
    ADD CONSTRAINT FK_FRIENDSHIPS_ON_REQUESTER FOREIGN KEY (requester_id) REFERENCES users (id);

ALTER TABLE message_attachments
    ADD CONSTRAINT FK_MESSAGEATTACHMENTS_ON_MESSAGE FOREIGN KEY (message_id) REFERENCES message (id);

ALTER TABLE message_reactions
    ADD CONSTRAINT FK_MESSAGEREACTIONS_ON_MESSAGE FOREIGN KEY (message_id) REFERENCES message (id);

ALTER TABLE message_reactions
    ADD CONSTRAINT FK_MESSAGEREACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE message
    ADD CONSTRAINT FK_MESSAGE_ON_CHANNEL FOREIGN KEY (channel_id) REFERENCES channel (id);

ALTER TABLE message
    ADD CONSTRAINT FK_MESSAGE_ON_SENDER FOREIGN KEY (sender_id) REFERENCES users (id);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE server_invites
    ADD CONSTRAINT FK_SERVERINVITES_ON_SERVER FOREIGN KEY (server_id) REFERENCES server (id);

ALTER TABLE server_members
    ADD CONSTRAINT FK_SERVER_MEMBERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE server_members
    ADD CONSTRAINT FK_SERVER_MEMBERS_ON_SERVER FOREIGN KEY (server_id) REFERENCES server (id);

ALTER TABLE server_members
    ADD CONSTRAINT FK_SERVER_MEMBERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);