--liquibase formatted sql
--changeset lllbllllb:init_tables

create table product_info
(
    build_info_id      uuid primary key not null,
    product_info       jsonb,
    created_date       timestamp,
    last_modified_date timestamp,
    version            bigint
);

create table build_info
(
    id                 uuid primary key not null,
    product_code       text,
    checksum           text,
    link               text,
    checksum_link      text,
    size               bigint,
    product_name       text,
    channel_name       text,
    channel_status     text,
    build_version      text,
    release_date       timestamp,
    full_number        text,
    status             text,
    created_date       timestamp,
    last_modified_date timestamp,
    round_id           uuid,
    version            bigint
);

create table round
(
    id                 uuid primary key not null,
    instance_id        text,
    created_date       timestamp,
    last_modified_date timestamp,
    version            bigint
);

create index if not exists product_code_idx on build_info (product_code);
create index if not exists full_number_idx on build_info (full_number);
