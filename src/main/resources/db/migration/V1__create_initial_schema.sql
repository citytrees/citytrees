create table ct_user
(
    id                 uuid         not null primary key,
    email              varchar(128) not null,
    pwd                varchar(128) not null,
    roles              jsonb        not null,
    status             varchar(16)  not null,
    creation_date_time timestamp    not null,
    first_name         varchar(128),
    last_name          varchar(128)
);

create unique index ct_user_email_uidx on ct_user (email);

create table ct_file
(
    id                 uuid         not null primary key,
    name               varchar(255) not null,
    mime_type          varchar(64)  not null,
    size               bigint       not null,
    hash               varchar(255) not null,
    creation_date_time timestamp    not null,
    user_id            uuid         not null
);

create index ct_file_hash_index on ct_file (hash);

create table ct_file_content
(
    id      uuid         not null primary key,
    content bytea        not null,
    hash    varchar(255) not null
);

create unique index ct_file_content_hash_unique_index on ct_file_content (hash);

create table ct_tree
(
    id                         uuid        not null primary key,
    user_id                    uuid        not null,
    status                     varchar(16) not null,
    geo_point                  geometry    not null,
    file_ids                   jsonb       not null default '[]'::jsonb,
    bark_condition             jsonb       not null default '[]'::jsonb,
    branches_condition         jsonb       not null default '[]'::jsonb,
    wood_type_id               uuid,
    state                      varchar(16),
    age                        integer,
    condition                  varchar(16),
    planting_type              varchar(16),
    comment                    text,
    diameter_of_crown          numeric,
    height_of_the_first_branch numeric,
    number_of_tree_trunks      int,
    tree_height                numeric,
    trunk_girth                numeric
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

create table ct_wood_type
(
    id      uuid               not null primary key,
    name    varchar(32) unique not null,
    user_id uuid               not null
)

-- todo #18 add enums, indexes