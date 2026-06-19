-- reviewsテーブルの作成
CREATE TABLE reviews (
    id NUMBER(10) PRIMARY KEY,
    rating NUMBER(1) NOT NULL,
    title VARCHAR2(50) NOT NULL,
    content VARCHAR2(200) NOT NULL,
    item_id NUMBER(10) NOT NULL,
    user_id NUMBER(10) NOT NULL,
    order_item_id NUMBER(10) NOT NULL,
    insert_date DATE DEFAULT CURRENT_DATE NOT NULL,
    CONSTRAINT fk_reviews_item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_order_item FOREIGN KEY (order_item_id) REFERENCES order_items(id),
    CONSTRAINT unique_order_item UNIQUE (order_item_id)
);

-- reviewsテーブル用シーケンスの作成
CREATE SEQUENCE seq_reviews START WITH 1 INCREMENT BY 1;
