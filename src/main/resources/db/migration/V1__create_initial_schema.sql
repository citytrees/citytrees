create type user_role as enum ('VOLUNTEER', 'MODERATOR', 'SUPERUSER');

create table ct_user
(
    id         uuid         not null primary key,
    email      varchar(128) not null,
    pwd        varchar(128) not null,
    roles      user_role    not null,
    first_name varchar(128),
    last_name  varchar(128)
);

create unique index ct_user_email_uidx on ct_user (email);
