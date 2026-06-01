-- 開発用初期データ投入SQL
-- adminユーザーは DataInitializer で作成する前提です。
-- このSQLでは、一般ユーザー・資産・貸出データを投入します。

BEGIN;

-- PostgreSQLでBCrypt形式のパスワードを生成するために使用
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================================================
-- Users
-- =========================================================

INSERT INTO users (
    username,
    password,
    email,
    full_name,
    role,
    active,
    created_at,
    updated_at
) VALUES
      (
          'tanaka',
          crypt('Tanaka_dev_2026!', gen_salt('bf', 10)),
          'tanaka@example.com',
          '田中太郎',
          'USER',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'suzuki',
          crypt('Suzuki_dev_2026!', gen_salt('bf', 10)),
          'suzuki@example.com',
          '鈴木花子',
          'USER',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'sato',
          crypt('Sato_dev_2026!', gen_salt('bf', 10)),
          'sato@example.com',
          '佐藤次郎',
          'USER',
          true,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      )
    ON CONFLICT (username) DO UPDATE SET
    password = EXCLUDED.password,
                                  email = EXCLUDED.email,
                                  full_name = EXCLUDED.full_name,
                                  role = EXCLUDED.role,
                                  active = EXCLUDED.active,
                                  updated_at = CURRENT_TIMESTAMP;

-- =========================================================
-- Assets
-- =========================================================

INSERT INTO assets (
    asset_code,
    name,
    description,
    category,
    status,
    location,
    created_at,
    updated_at
) VALUES
      (
          'PC001',
          'ノートPC ThinkPad X1',
          '開発用ノートPC Core i7/16GB RAM',
          'PC',
          'AVAILABLE',
          'オフィス3F',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'PC002',
          'ノートPC MacBook Pro',
          'デザイナー用ノートPC M2/32GB RAM',
          'PC',
          'AVAILABLE',
          'オフィス3F',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'PC003',
          'デスクトップPC',
          '検証用デスクトップPC',
          'PC',
          'AVAILABLE',
          'サーバールーム',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'MON001',
          'モニター Dell 27インチ',
          '4Kモニター 27インチ',
          'モニター',
          'AVAILABLE',
          'オフィス3F',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'MON002',
          'モニター LG 32インチ',
          'ウルトラワイドモニター',
          'モニター',
          'AVAILABLE',
          'オフィス3F',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'TAB001',
          'iPad Pro 12.9',
          'プレゼン用タブレット',
          'タブレット',
          'AVAILABLE',
          '会議室A',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'CAM001',
          'Webカメラ Logicool',
          'Web会議用カメラ 4K対応',
          '周辺機器',
          'AVAILABLE',
          '会議室B',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'KEY001',
          'メカニカルキーボード',
          '英語配列 青軸',
          '周辺機器',
          'AVAILABLE',
          '倉庫',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'MOU001',
          'ワイヤレスマウス',
          'Logicool MX Master 3',
          '周辺機器',
          'AVAILABLE',
          '倉庫',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'PRJ001',
          'プロジェクター Epson',
          '会議室用プロジェクター 4000lm',
          'プロジェクター',
          'AVAILABLE',
          '会議室A',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      )
    ON CONFLICT (asset_code) DO UPDATE SET
    name = EXCLUDED.name,
                                    description = EXCLUDED.description,
                                    category = EXCLUDED.category,
                                    location = EXCLUDED.location,
                                    updated_at = CURRENT_TIMESTAMP;

-- =========================================================
-- Loans
-- =========================================================
-- 再実行時に貸出データが重複しないよう、開発用のACTIVE貸出を一度削除します。
-- その後、関連する資産ステータスをAVAILABLEに戻してから再投入します。

DELETE FROM loans
WHERE remarks IN (
                  '新プロジェクト用',
                  'デュアルモニター環境構築',
                  'デザイン作業用'
    );

UPDATE assets
SET status = 'AVAILABLE',
    updated_at = CURRENT_TIMESTAMP
WHERE asset_code IN ('PC001', 'MON001', 'PC002');

-- PC001 -> tanaka
INSERT INTO loans (
    asset_id,
    user_id,
    loan_date,
    expected_return_date,
    actual_return_date,
    status,
    remarks,
    created_at,
    updated_at
)
SELECT
    a.id,
    u.id,
    DATE '2026-06-01',
    DATE '2026-07-01',
    NULL,
    'ACTIVE',
    '新プロジェクト用',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM assets a
         JOIN users u ON u.username = 'tanaka'
WHERE a.asset_code = 'PC001';

UPDATE assets
SET status = 'LOANED',
    updated_at = CURRENT_TIMESTAMP
WHERE asset_code = 'PC001';

-- MON001 -> tanaka
INSERT INTO loans (
    asset_id,
    user_id,
    loan_date,
    expected_return_date,
    actual_return_date,
    status,
    remarks,
    created_at,
    updated_at
)
SELECT
    a.id,
    u.id,
    DATE '2026-06-01',
    DATE '2026-07-01',
    NULL,
    'ACTIVE',
    'デュアルモニター環境構築',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM assets a
         JOIN users u ON u.username = 'tanaka'
WHERE a.asset_code = 'MON001';

UPDATE assets
SET status = 'LOANED',
    updated_at = CURRENT_TIMESTAMP
WHERE asset_code = 'MON001';

-- PC002 -> suzuki
INSERT INTO loans (
    asset_id,
    user_id,
    loan_date,
    expected_return_date,
    actual_return_date,
    status,
    remarks,
    created_at,
    updated_at
)
SELECT
    a.id,
    u.id,
    DATE '2026-06-02',
    DATE '2026-08-02',
    NULL,
    'ACTIVE',
    'デザイン作業用',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM assets a
         JOIN users u ON u.username = 'suzuki'
WHERE a.asset_code = 'PC002';

UPDATE assets
SET status = 'LOANED',
    updated_at = CURRENT_TIMESTAMP
WHERE asset_code = 'PC002';

COMMIT;