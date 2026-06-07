-- 開発用初期データ投入SQL（2026年 IT企業実務ベース）
-- adminユーザーは DataInitializer で作成します
BEGIN;

-- PostgreSQLでBCrypt形式のパスワードを生成するために使用
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================================================
-- クリーンアップ (子テーブルから順番に削除・初期化))
-- =========================================================
-- TRUNCATE TABLE loans RESTART IDENTITY CASCADE;
-- TRUNCATE TABLE assets RESTART IDENTITY CASCADE;
-- DELETE FROM users WHERE role = 'USER';

-- =========================================================
-- Users (社内アカウント)
-- =========================================================
INSERT INTO users (
    username, password, email, full_name, role, status, created_at, updated_at
) VALUES
      (
          'k.tanaka',
          crypt('Tanaka_dev_2026!', gen_salt('bf', 10)),
          'k.tanaka@example.com',
          '田中 健太',
          'USER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      ),
      (
          'h.suzuki',
          crypt('Suzuki_dev_2026!', gen_salt('bf', 10)),
          'h.suzuki@example.com',
          '鈴木 花子',
          'USER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      ),
      (
          'j.sato',
          crypt('Sato_dev_2026!', gen_salt('bf', 10)),
          'j.sato@example.com',
          '佐藤 次郎',
          'USER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      )
    ON CONFLICT (username) DO UPDATE SET
    password = EXCLUDED.password,
    email = EXCLUDED.email,
    full_name = EXCLUDED.full_name,
    role = EXCLUDED.role,
    status = EXCLUDED.status,
    updated_at = CURRENT_TIMESTAMP;

-- =========================================================
-- Assets (資産管理ラベル、具体的なスペック、保管場所のリアル化)
-- =========================================================
INSERT INTO assets (
    asset_code, name, description, category, status, location, created_at, updated_at
) VALUES
      -- ノートPC (管理番号は固定長)
      ('PC-2025-001', 'MacBook Pro 14" (M3 Pro)', '開発用: M3 Pro/32GB RAM/512GB SSD/US配列', 'NOTE_PC', 'AVAILABLE', '渋谷オフィス-IT資産ロッカーB', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('PC-2025-002', 'ThinkPad T14 Gen 4', '開発用: Core i7/32GB RAM/1TB SSD/日本語配列', 'NOTE_PC', 'AVAILABLE', '渋谷オフィス-IT資産ロッカーB', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('PC-2026-001', 'MacBook Air 13" (M3)', '一般業務・検証用: M3/16GB RAM/512GB SSD', 'NOTE_PC', 'AVAILABLE', '渋谷オフィス-IT資産ロッカーA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      -- モニター
      ('MON-27-001', 'Dell U2723QE 27インチ 4K', '4Kハブモニター (USB-C給電・LANポート搭載)', 'MONITOR', 'AVAILABLE', '渋谷オフィス-執務エリアA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('MON-34-001', 'LG 34WN75C-B 34インチ', '曲面ウルトラワイドモニター (解像度: 3440×1440)', 'MONITOR', 'AVAILABLE', '渋谷オフィス-執務エリアB', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      -- ガジェット・周辺機器
      ('DEV-TAB-01', 'iPad Pro 11インチ (M2)', 'モバイルアプリ動作検証用 (Wi-Fiモデル/128GB)', 'TABLET', 'AVAILABLE', '目黒ラボ-検証機保管棚2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('DEV-CAM-01', 'Logicool StreamCam', 'リモート面接・動検証用 Webカメラ (1080P/60FPS)', 'PERIPHERAL', 'AVAILABLE', '渋谷オフィス-総務カウンター', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('DEV-KEY-01', 'HHKB Professional HYBRID', '貸出用キーボード: 英語配列/墨/Type-S', 'PERIPHERAL', 'AVAILABLE', '渋谷オフィス-IT資産ロッカーA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('DEV-MOU-01', 'Logicool MX Master 3S', '貸出用高機能マウス: グラファイト', 'PERIPHERAL', 'AVAILABLE', '渋谷オフィス-IT資産ロッカーA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

      -- 会議室専用品
      ('PRJ-001', 'Epson EB-FH52', '会議室常設用プロジェクター (4000lm/フルHD)', 'PROJECTOR', 'AVAILABLE', '渋谷オフィス-大会議室A', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (asset_code) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    category = EXCLUDED.category,
    location = EXCLUDED.location,
    updated_at = CURRENT_TIMESTAMP;

-- =========================================================
-- Loans (貸出理由)
-- =========================================================

-- PC-2025-001 (MacBook Pro) -> k.tanaka (田中 健太)
-- 理由: 新規プロジェクトへのアサインに伴う開発機支給
INSERT INTO loans (asset_id, user_id, loan_date, expected_return_date, actual_return_date, status, remarks, created_at, updated_at)
SELECT a.id, u.id, DATE '2026-04-01', DATE '2027-03-31', NULL, 'ACTIVE', '新規開発プロジェクトアサインに伴う標準PC支給', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM assets a JOIN users u ON u.username = 'k.tanaka' WHERE a.asset_code = 'PC-2025-001';

UPDATE assets SET status = 'LOANED', updated_at = CURRENT_TIMESTAMP WHERE asset_code = 'PC-2025-001';

-- MON-27-001 (Dell 4Kモニター) -> k.tanaka (田中 健太)
-- 理由: リモートワーク環境（在宅勤務）の強化用
INSERT INTO loans (asset_id, user_id, loan_date, expected_return_date, actual_return_date, status, remarks, created_at, updated_at)
SELECT a.id, u.id, DATE '2026-04-05', DATE '2027-03-31', NULL, 'ACTIVE', '在宅勤務（リモートワーク）環境構築のためのモニター貸出申請', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM assets a JOIN users u ON u.username = 'k.tanaka' WHERE a.asset_code = 'MON-27-001';

UPDATE assets SET status = 'LOANED', updated_at = CURRENT_TIMESTAMP WHERE asset_code = 'MON-27-001';

-- DEV-TAB-01 (iPad Pro) -> h.suzuki (鈴木 花子)
-- 理由: モバイルアプリのリリース前検証（短期貸出）
INSERT INTO loans (asset_id, user_id, loan_date, expected_return_date, actual_return_date, status, remarks, created_at, updated_at)
SELECT a.id, u.id, DATE '2026-06-01', DATE '2026-06-15', NULL, 'ACTIVE', 'iOS向け新機能のステージング環境における動作検証、UI確認', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM assets a JOIN users u ON u.username = 'h.suzuki' WHERE a.asset_code = 'DEV-TAB-01';

UPDATE assets SET status = 'LOANED', updated_at = CURRENT_TIMESTAMP WHERE asset_code = 'DEV-TAB-01';

COMMIT;