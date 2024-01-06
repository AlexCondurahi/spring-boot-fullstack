CREATE SEQUENCE customer_id_seq;
CREATE TABLE customer(
    id INTEGER DEFAULT nextval('customer_id_seq') NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    age INT NOT NULL
);