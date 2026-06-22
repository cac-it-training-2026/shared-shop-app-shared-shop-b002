-- お気に入りテーブル
CREATE TABLE favorites (
    id NUMBER(10) PRIMARY KEY,
    user_id NUMBER(10) NOT NULL,
    item_id NUMBER(10) NOT NULL,
    CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_favorites_item FOREIGN KEY (item_id) REFERENCES items(id)
);

-- お気に入りID用シーケンス
CREATE SEQUENCE seq_favorites START WITH 1 INCREMENT BY 1;
