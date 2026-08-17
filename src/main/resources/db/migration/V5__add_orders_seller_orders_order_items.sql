CREATE TABLE orders (
    id          BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES users(id),
    status      VARCHAR(20) NOT NULL
                CHECK (status IN ('PENDING', 'PAID', 'PAYMENT_FAILED')),
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL
);

CREATE TABLE seller_orders (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT NOT NULL REFERENCES orders(id),
    seller_id  BIGINT NOT NULL REFERENCES seller_profiles(id),
    status     VARCHAR(20) NOT NULL
               CHECK (status IN ('PENDING', 'CONFIRMED')),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE order_items (
    id                BIGSERIAL PRIMARY KEY,
    seller_order_id   BIGINT NOT NULL REFERENCES seller_orders(id),
    product_id        BIGINT NOT NULL REFERENCES products(id),
    quantity          INTEGER NOT NULL CHECK (quantity > 0),
    price_at_purchase NUMERIC(10,2) NOT NULL CHECK (price_at_purchase >= 0)
);

CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_seller_orders_order_id ON seller_orders(order_id);
CREATE INDEX idx_seller_orders_seller_id ON seller_orders(seller_id);
CREATE INDEX idx_order_items_seller_order_id ON order_items(seller_order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);