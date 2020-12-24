create or replace function random_integer_in_range(integer, integer) returns integer
    language sql as
$$
select floor(($1 + ($2 - $1 + 1) * random()))::integer
$$;

create or replace function random_timestamp_in_range(timestamptz, timestamptz) returns timestamptz
    language sql as
$$
select $1 + floor((extract(epoch from $2 - $1) + 1) * random())::integer::text::interval
$$;

/*
insert into reporting.cost_per_piece (brand_id, product_type, effective_date,
                                      effective_date_year,
                                      effective_date_month, price)
select random_integer_in_range(1, 1e9::integer),
       'POSTCARD'::reporting.product_type,
       random_timestamp_in_range('2018-01-01'::timestamptz,
                                 '2020-12-31'::timestamptz),
       extract(year from random_timestamp_in_range('2018-01-01'::timestamptz,
                                                   '2020-12-31'::timestamptz)),
       extract(month from random_timestamp_in_range('2018-01-01'::timestamptz,
                                                    '2020-12-31'::timestamptz)),
       random_integer_in_range(1, 4)::numeric(4, 2)
from generate_series(1, 50);
*/
