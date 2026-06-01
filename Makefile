.PHONY: help db-up db-down db-logs db-clean init-data init-users init-assets init-loans test-api

# デフォルトターゲット
.DEFAULT_GOAL := help

# カラー定義
COLOR_RESET = \033[0m
COLOR_BOLD = \033[1m
COLOR_GREEN = \033[32m
COLOR_YELLOW = \033[33m
COLOR_BLUE = \033[34m

# 設定
API_URL = http://localhost:8080/api
COMPOSE_FILE = compose.yaml

## ヘルプ表示
help:
	@echo "$(COLOR_GREEN)初期データ投入:$(COLOR_RESET)"
	@echo "  make init-data    - 全ての初期データを投入"
	@echo "  make init-data-sql - SQLで開発用初期データを投入"
	@echo "  make init-users   - ユーザーデータを投入"
	@echo "  make init-assets  - 資産データを投入"
	@echo "  make init-loans   - 貸出データを投入"

## Docker関連コマンド

# PostgreSQLコンテナを起動
db-up:
	@echo "$(COLOR_BLUE)PostgreSQLコンテナを起動中...$(COLOR_RESET)"
	docker compose -f $(COMPOSE_FILE) up -d
	@echo "$(COLOR_GREEN)✓ PostgreSQLが起動しました$(COLOR_RESET)"
	@sleep 3
	docker compose ps

# PostgreSQLコンテナを停止
db-down:
	@echo "$(COLOR_YELLOW)PostgreSQLコンテナを停止中...$(COLOR_RESET)"
	docker compose -f $(COMPOSE_FILE) down
	@echo "$(COLOR_GREEN)✓ PostgreSQLが停止しました$(COLOR_RESET)"

# PostgreSQLログを表示
db-logs:
	docker compose -f $(COMPOSE_FILE) logs -f postgres

# PostgreSQLコンテナとボリュームを削除
db-clean:
	@echo "$(COLOR_YELLOW)PostgreSQLコンテナとボリュームを削除中...$(COLOR_RESET)"
	docker compose -f $(COMPOSE_FILE) down -v
	@echo "$(COLOR_GREEN)✓ クリーンアップが完了しました$(COLOR_RESET)"

## 初期データ投入

# SQLで開発用初期データを投入
init-data-sql:
	@echo "$(COLOR_BLUE)SQLで開発用初期データを投入中...$(COLOR_RESET)"
	docker compose -f $(COMPOSE_FILE) exec -T postgres psql -U loan_user -d loan_db < docs/dev-seed.sql
	@echo "$(COLOR_GREEN)✓ SQLでの初期データ投入が完了しました$(COLOR_RESET)"

## API動作確認

# jqでAPIの動作確認
test-api:
	@echo "$(COLOR_BLUE)=== DB接続確認 ===$(COLOR_RESET)"
	@curl -s $(API_URL)/health/db | jq .
	@echo ""
	@echo "$(COLOR_BLUE)=== ユーザー一覧 ===$(COLOR_RESET)"
	@curl -s $(API_URL)/users | jq '.[] | {id, username, fullName, role}'
	@echo ""
	@echo "$(COLOR_BLUE)=== 資産一覧（利用可能） ===$(COLOR_RESET)"
	@curl -s $(API_URL)/assets/status/AVAILABLE | jq '.[] | {id, assetCode, name, status}'
	@echo ""
	@echo "$(COLOR_BLUE)=== 資産一覧（貸出中） ===$(COLOR_RESET)"
	@curl -s $(API_URL)/assets/status/LOANED | jq '.[] | {id, assetCode, name, status}'
	@echo ""
	@echo "$(COLOR_BLUE)=== 貸出中一覧 ===$(COLOR_RESET)"
	@curl -s $(API_URL)/loans/active | jq '.[] | {id, asset: .asset.name, user: .user.fullName, loanDate, expectedReturnDate}'
	@echo ""