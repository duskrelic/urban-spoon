drop schema if exists reporting cascade;
create schema reporting;
create type reporting.product_type as enum ('POSTCARD', 'CATALOG');
create table reporting.cost_per_piece
(
    id                   serial                                 not null
        constraint cost_per_piece_pkey
            primary key,
    brand_id             integer                                not null,
    product_type         reporting.product_type                 not null,
    effective_date       date                                   not null,
    effective_date_year  integer                                not null,
    effective_date_month integer                                not null,
    price                numeric(4, 2)                          not null,
    created_on           timestamp with time zone default now() not null,
    constraint cost_per_piece_product_type_price_date
        unique (brand_id, product_type, effective_date)
);
