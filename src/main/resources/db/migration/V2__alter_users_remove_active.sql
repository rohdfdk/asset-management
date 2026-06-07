-- 1. active カラムを削除
ALTER TABLE users DROP COLUMN active;

-- 2. status カラムを文字列型で追加（デフォルト値は 'ACTIVE'）
ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';