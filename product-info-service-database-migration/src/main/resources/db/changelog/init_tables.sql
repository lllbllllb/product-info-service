--liquibase formatted sql
--changeset lllbllllb:init_tables

create table build_info
(
    id                 uuid primary key not null,
    product_code       text,
    product_info       jsonb,
    checksum           text,
    link               text,
    checksum_link      text,
    size               text,
    product_name       text,
    channel_name       text,
    channel_status     text,
    build_version      text,
    release_date       date,
    full_number        text unique,
    created_date       date,
    last_modified_date date,
    version            bigint
);

create index if not exists product_code_idx on build_info (product_code);
