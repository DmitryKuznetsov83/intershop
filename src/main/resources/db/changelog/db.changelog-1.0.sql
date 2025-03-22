-- changeset Dmitry Kuznetsov: 1
create table item
(
    id integer generated by default as identity
        constraint item_pk
            primary key,
    title       varchar,
    description varchar,
    price       integer,
    image       bytea,
    has_image   boolean
);

alter table item
    owner to postgres;