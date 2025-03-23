-- changeset Dmitry Kuznetsov: 1
create table cart_item
(
    item_id     integer constraint cart_item_pk primary key,
    quantity    integer,
    CONSTRAINT cart_item_item_id_fk FOREIGN KEY (item_id) REFERENCES item (id)
);

alter table cart_item
    owner to postgres;