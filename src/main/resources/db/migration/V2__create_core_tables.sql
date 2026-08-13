CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) UNIQUE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE seller_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    shop_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_seller_profiles_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_products_seller
        FOREIGN KEY (seller_id) REFERENCES seller_profiles(id)
);

CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_carts_customer
        FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_cart_items_cart
        FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT fk_cart_items_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);