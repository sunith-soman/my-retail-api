-- Create keyspace
CREATE KEYSPACE IF NOT EXISTS my_retail WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

-- Create table
CREATE TABLE IF NOT EXISTS my_retail.price (
product_id int PRIMARY KEY,
price double,
currency_code text,
last_update_timestamp timestamp
);

CREATE TABLE IF NOT EXISTS my_retail.user (
user_name text PRIMARY KEY,
password text
);


-- Insert data
INSERT INTO my_retail.price
(product_id, price, currency_code, last_update_timestamp)
VALUES (13860428, 13.21, 'USD', toTimeStamp(now()));
INSERT INTO my_retail.price
(product_id, price, currency_code, last_update_timestamp)
VALUES (54456119, 21.56, 'USD', toTimeStamp(now()));
INSERT INTO my_retail.price
(product_id, price, currency_code, last_update_timestamp)
VALUES (13264003, 32.13, 'USD', toTimeStamp(now()));
INSERT INTO my_retail.price
(product_id, price, currency_code, last_update_timestamp)
VALUES (12954218, 11.32, 'USD', toTimeStamp(now()));

INSERT INTO my_retail.user
(user_name, password)
VALUES ('consumer1', '$2a$10$ZXhGpZyz.HAfFJ5i1kNiKu7wZDFTWA0QM5Em8YDFdFyWj4AlKy/Ua');
INSERT INTO my_retail.user
(user_name, password)
VALUES ('consumer2', '$2a$10$TgVCVt4.UX5iinS6yT1LZOjy6rJwqm1TQynbY9ibxzxRJ9Z9GG2QW');