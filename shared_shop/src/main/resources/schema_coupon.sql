-- クーポンテーブルの作成
CREATE TABLE coupon (
    coupon_id NUMBER PRIMARY KEY,
    coupon_code VARCHAR2(50) UNIQUE NOT NULL,
    coupon_name VARCHAR2(100),
    discount_type VARCHAR2(20) NOT NULL,
    discount_value NUMBER NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    enabled NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- クーポンID用シーケンスの作成
CREATE SEQUENCE seq_coupon START WITH 1 INCREMENT BY 1;

-- 注文テーブルへのカラム追加（DB保存用）
ALTER TABLE orders ADD (
    coupon_code VARCHAR2(50),
    discount_amount NUMBER,
    discounted_total NUMBER
);
