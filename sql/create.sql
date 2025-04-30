drop table shipmentContains;
drop table manufacturingProduces;
drop table manufacturingUses;
drop table manufacturing;
drop table consistsOf;
drop table suppliesProduct;
drop table suppliedBy;
drop table shipment;
drop table batch;
drop table store;
drop table product;
drop table supplier;

create table store(
    store_id int primary key,
    location varchar(255) not null
);

create table product(
    product_id int primary key,
    name varchar(255) not null,
    is_end_product number(1) not null,
    check (is_end_product IN (0, 1))
);

create table supplier(
    supplier_id int primary key,
    name varchar(255) not null,
    location varchar(255) not null
);

create table shipment(
    shipment_id int primary key,
    supplier_id int not null,
    store_id int not null,
    shipment_date date not null,
    foreign key (supplier_id) references supplier(supplier_id),
    foreign key (store_id) references store(store_id)
);

create table batch(
    batch_id int primary key,
    product_id int not null,
    serial_number varchar(255),
    lot_number varchar(255),
    quantity int not null,
    check (quantity > 0),
    unique (serial_number),
    unique (lot_number),
    foreign key (product_id) references product(product_id)
);

create table shipmentContains(
    shipment_id int,
    batch_id int,
    quantity int not null,
    price_per_unit decimal(10,2) not null,
    check (quantity > 0),
    check (price_per_unit > 0),
    primary key (shipment_id, batch_id),
    foreign key (shipment_id) references shipment(shipment_id),
    foreign key (batch_id) references batch(batch_id)
);

create table manufacturing(
    manufacturing_id int primary key,
    supplier_id int not null,
    manufacturing_date date not null,
    foreign key (supplier_id) references supplier(supplier_id)
);

create table manufacturingProduces(
    manufacturing_id int,
    batch_id int,
    primary key (manufacturing_id, batch_id),
    foreign key (manufacturing_id) references manufacturing(manufacturing_id),
    foreign key (batch_id) references batch(batch_id)
);

create table manufacturingUses(
    manufacturing_id int,
    product_id int,
    quantity_used int not null,
    check (quantity_used > 0),
    primary key (manufacturing_id, product_id),
    foreign key (manufacturing_id) references manufacturing(manufacturing_id),
    foreign key (product_id) references product(product_id)
);

create table suppliesProduct(
    supplier_id int,
    product_id int,
    current_price decimal(10,2) not null,
    check (current_price > 0),
    primary key (supplier_id, product_id),
    foreign key (supplier_id) references supplier(supplier_id),
    foreign key (product_id) references product(product_id)
);

create table suppliedBy(
    supplier_id int,
    recipient_supplier_id int,
    product_id int not null,
    primary key (supplier_id, recipient_supplier_id, product_id),
    foreign key (supplier_id) references supplier(supplier_id),
    foreign key (recipient_supplier_id) references supplier(supplier_id),
    foreign key (product_id) references product(product_id)
);

create table consistsOf(
    parent_product_id int,
    component_product_id int,
    quantity int not null,
    check (quantity > 0),
    primary key (parent_product_id, component_product_id),
    foreign key (parent_product_id) references product(product_id),
    foreign key (component_product_id) references product(product_id)
);