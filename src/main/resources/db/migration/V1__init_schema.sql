-- ユーザーテーブル
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL,
                       full_name VARCHAR(50) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       active BOOLEAN NOT NULL DEFAULT true,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 資産テーブル
CREATE TABLE assets (
                        id BIGSERIAL PRIMARY KEY,
                        asset_code VARCHAR(50) NOT NULL UNIQUE,
                        name VARCHAR(100) NOT NULL,
                        description VARCHAR(500),
                        category VARCHAR(50) NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        location VARCHAR(200),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 貸出テーブル
CREATE TABLE loans (
                       id BIGSERIAL PRIMARY KEY,
                       asset_id BIGINT NOT NULL,
                       user_id BIGINT NOT NULL,
                       loan_date DATE NOT NULL,
                       expected_return_date DATE NOT NULL,
                       actual_return_date DATE,
                       status VARCHAR(20) NOT NULL,
                       remarks VARCHAR(500),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE RESTRICT,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- インデックス
CREATE INDEX idx_assets_status ON assets(status);
CREATE INDEX idx_loans_status ON loans(status);
CREATE INDEX idx_loans_user_id ON loans(user_id);
CREATE INDEX idx_loans_asset_id ON loans(asset_id);