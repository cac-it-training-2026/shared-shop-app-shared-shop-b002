-- Account Lock Feature Setup SQL (for Oracle Database)

-- Add login_attempt_count column
ALTER TABLE users ADD (login_attempt_count NUMBER(10) DEFAULT 0 NOT NULL);

-- Add is_locked column
-- 0: Unlocked, 1: Locked
ALTER TABLE users ADD (is_locked NUMBER(1) DEFAULT 0 NOT NULL);

-- Add locked_time column
ALTER TABLE users ADD (locked_time TIMESTAMP);

-- Comments for columns
COMMENT ON COLUMN users.login_attempt_count IS 'ログイン失敗回数';
COMMENT ON COLUMN users.is_locked IS 'アカウントロック状態フラグ (0:未ロック, 1:ロック中)';
COMMENT ON COLUMN users.locked_time IS 'アカウントロック時刻';
