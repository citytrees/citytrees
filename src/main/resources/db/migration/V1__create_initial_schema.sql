create table ct_user
(
    id         uuid         not null primary key,
    email      varchar(128) not null,
    pwd        varchar(128) not null,
    roles      jsonb        not null,
    status     varchar(16)  not null,
    first_name varchar(128),
    last_name  varchar(128)
);

create unique index ct_user_email_uidx on ct_user (email);

create table ct_file
(
    id        uuid not null primary key,
    name      varchar(255),
    mime_type varchar(64),
    size      bigint,
    hash      varchar(255),
    user_id   uuid
);

create index ct_file_hash_index on ct_file (hash);

create table ct_tree
(
    id        uuid        not null primary key,
    user_id   uuid        not null,
    status    varchar(16) not null,
    geo_point geometry    not null,
    file_ids  jsonb       not null default '[]'::jsonb
);

create unique index ct_tree_user_id_uidx on ct_tree (id);

create table ct_user_password_reset
(
    user_id uuid         not null primary key,
    email   varchar(128) not null,
    token   varchar(128) not null,
    status  varchar(16)  not null
);

create unique index ct_user_password_reset_email_uidx on ct_user_password_reset (email);
