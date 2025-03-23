-- changeset Dmitry Kuznetsov: 1
create table orders
(
    id          integer generated by default as identity constraint order_pk primary key,
    created     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

alter table orders
    owner to postgres;

-- changeset Dmitry Kuznetsov: 2
create table order_item
(
    id          integer generated by default as identity constraint order_item_pk primary key,
    order_id    integer not null,
    item_id     integer not null,
    quantity    integer not null,
    CONSTRAINT order_item_order_id_fk FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT order_item_item_id_fk FOREIGN KEY (item_id) REFERENCES item (id)
);

alter table order_item
    owner to postgres;
