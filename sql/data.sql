INSERT INTO product (product_id, name, is_end_product) VALUES (1772, 'Ozgon rice', 1);
INSERT INTO product (product_id, name, is_end_product) VALUES (1913, 'Talas beans', 1);
INSERT INTO product (product_id, name, is_end_product) VALUES (1269, 'Issyk-Kul apples', 1);
INSERT INTO product (product_id, name, is_end_product) VALUES (1585, 'Summation Cereal', 1);
INSERT INTO product (product_id, name, is_end_product) VALUES (1637, 'KitchenPro Toaster', 1);

INSERT INTO supplier (supplier_id, name, location) VALUES (2791, 'Kurshab Inc.', '13 Manasa Street');
INSERT INTO supplier (supplier_id, name, location) VALUES (2634, 'Matkasymov', '18 Krasnaya Street');
INSERT INTO supplier (supplier_id, name, location) VALUES (2884, 'Giantic', 'Jalal-Abad');
INSERT INTO supplier (supplier_id, name, location) VALUES (2324, 'Ocean', 'Bishkek');
INSERT INTO supplier (supplier_id, name, location) VALUES (2744, 'MyBank', '198 Chui Avenue');

INSERT INTO store (store_id, location) VALUES (3423, '18 Gagarina Street');
INSERT INTO store (store_id, location) VALUES (3366, '98 Chui Avenue');

INSERT INTO batch (batch_id, product_id, serial_number, lot_number, quantity) VALUES (4776, 1772, NULL, 'LOT4138', 38);
INSERT INTO batch (batch_id, product_id, serial_number, lot_number, quantity) VALUES (4350, 1913, NULL, 'LOT3567', 79);
INSERT INTO batch (batch_id, product_id, serial_number, lot_number, quantity) VALUES (4241, 1269, NULL, 'LOT1799', 46);
INSERT INTO batch (batch_id, product_id, serial_number, lot_number, quantity) VALUES (4137, 1585, NULL, 'LOT1739', 124);
INSERT INTO batch (batch_id, product_id, serial_number, lot_number, quantity) VALUES (4337, 1637, 'SN6316', NULL, 1);

INSERT INTO shipment (shipment_id, supplier_id, store_id, shipment_date) VALUES (5837, 2634, 3423, TO_DATE('2025-04-13', 'YYYY-MM-DD'));
INSERT INTO shipment (shipment_id, supplier_id, store_id, shipment_date) VALUES (5730, 2324, 3423, TO_DATE('2025-04-06', 'YYYY-MM-DD'));
INSERT INTO shipment (shipment_id, supplier_id, store_id, shipment_date) VALUES (5162, 2324, 3423, TO_DATE('2025-04-02', 'YYYY-MM-DD'));
INSERT INTO shipment (shipment_id, supplier_id, store_id, shipment_date) VALUES (5137, 2791, 3366, TO_DATE('2025-04-10', 'YYYY-MM-DD'));
INSERT INTO shipment (shipment_id, supplier_id, store_id, shipment_date) VALUES (5372, 2744, 3423, TO_DATE('2025-03-30', 'YYYY-MM-DD'));

INSERT INTO shipmentContains (shipment_id, batch_id, quantity, price_per_unit) VALUES (5837, 4776, 20, 94.26);
INSERT INTO shipmentContains (shipment_id, batch_id, quantity, price_per_unit) VALUES (5730, 4350, 37, 145.80);
INSERT INTO shipmentContains (shipment_id, batch_id, quantity, price_per_unit) VALUES (5162, 4241, 19, 118.44);
INSERT INTO shipmentContains (shipment_id, batch_id, quantity, price_per_unit) VALUES (5137, 4137, 48, 31.31);
INSERT INTO shipmentContains (shipment_id, batch_id, quantity, price_per_unit) VALUES (5372, 4337, 1, 49.46);

INSERT INTO manufacturing (manufacturing_id, supplier_id, manufacturing_date) VALUES (6418, 2884, TO_DATE('2025-03-09', 'YYYY-MM-DD'));
INSERT INTO manufacturing (manufacturing_id, supplier_id, manufacturing_date) VALUES (6420, 2884, TO_DATE('2025-03-12', 'YYYY-MM-DD'));

INSERT INTO manufacturingProduces (manufacturing_id, batch_id) VALUES (6418, 4137);
INSERT INTO manufacturingProduces (manufacturing_id, batch_id) VALUES (6420, 4337);

INSERT INTO manufacturingUses (manufacturing_id, product_id, quantity_used) VALUES (6418, 1772, 2);
INSERT INTO manufacturingUses (manufacturing_id, product_id, quantity_used) VALUES (6418, 1913, 1);

INSERT INTO consistsOf (parent_product_id, component_product_id, quantity) VALUES (1585, 1772, 2);
INSERT INTO consistsOf (parent_product_id, component_product_id, quantity) VALUES (1585, 1913, 1);

INSERT INTO suppliesProduct (supplier_id, product_id, current_price) VALUES (2634, 1772, 123.25);
INSERT INTO suppliesProduct (supplier_id, product_id, current_price) VALUES (2634, 1913, 24.61);
INSERT INTO suppliesProduct (supplier_id, product_id, current_price) VALUES (2744, 1269, 26.92);
INSERT INTO suppliesProduct (supplier_id, product_id, current_price) VALUES (2884, 1585, 21.77);
INSERT INTO suppliesProduct (supplier_id, product_id, current_price) VALUES (2884, 1637, 142.14);

INSERT INTO suppliedBy (supplier_id, recipient_supplier_id, product_id) VALUES (2791, 2884, 1772);
INSERT INTO suppliedBy (supplier_id, recipient_supplier_id, product_id) VALUES (2634, 2324, 1913);
