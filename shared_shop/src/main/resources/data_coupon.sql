-- 重複登録を防ぐため、MERGE文を使用してデータを投入する

-- 1つ目：ハッピークーポン (happy / 50円引き)
MERGE INTO coupon c
USING (SELECT 'happy' as code FROM dual) src
ON (c.coupon_code = src.code)
WHEN NOT MATCHED THEN
  INSERT (coupon_id, coupon_code, coupon_name, discount_type, discount_value, enabled, created_at)
  VALUES (seq_coupon.NEXTVAL, 'happy', 'ハッピークーポン', 'FIXED', 50, 1, CURRENT_TIMESTAMP);

-- 2つ目：あひるクーポン (Duckle / 10%引き)
MERGE INTO coupon c
USING (SELECT 'Duckle' as code FROM dual) src
ON (c.coupon_code = src.code)
WHEN NOT MATCHED THEN
  INSERT (coupon_id, coupon_code, coupon_name, discount_type, discount_value, enabled, created_at)
  VALUES (seq_coupon.NEXTVAL, 'Duckle', 'あひるクーポン', 'PERCENT', 10, 1, CURRENT_TIMESTAMP);

COMMIT;
