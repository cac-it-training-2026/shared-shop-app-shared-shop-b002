-- 1つ目：ハッピークーポン (happy / 50円引き)
INSERT INTO coupon (coupon_id, coupon_code, coupon_name, discount_type, discount_value, enabled, created_at)
VALUES (seq_coupon.NEXTVAL, 'happy', 'ハッピークーポン', 'FIXED', 50, 1, CURRENT_TIMESTAMP);

-- 2つ目：あひるクーポン (Duckle / 10%引き)
INSERT INTO coupon (coupon_id, coupon_code, coupon_name, discount_type, discount_value, enabled, created_at)
VALUES (seq_coupon.NEXTVAL, 'Duckle', 'あひるクーポン', 'PERCENT', 10, 1, CURRENT_TIMESTAMP);

COMMIT;
