.PHONY: help db-up db-down db-logs db-clean db-fresh

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
	@echo "$(COLOR_BLUE)使用可能なコマンド一覧:$(COLOR_RESET)"
	@echo "  make init-data-sql - SQLで開発用初期データを投入"
	@echo "  make db-up        - PostgreSQLコンテナを起動 (バックグラウンド)"
	@echo "  make db-down      - PostgreSQLコンテナを停止"
	@echo "  make db-logs      - PostgreSQLのリアルタイムログを表示"
	@echo "  make db-clean     - PostgreSQLコンテナとボリューム(データ)を完全に削除"
	@echo "  make db-fresh  - ボリュームを削除してクリーンな状態で再起動 (マイグレーション再実行用)"

## Docker関連コマンド

# PostgreSQLコンテナを起動
db-up:
	@echo "$(COLOR_BLUE)PostgreSQLコンテナを起動中...$(COLOR_RESET)"
	docker compose -f $(COMPOSE_FILE) up -d --build --wait
	@echo "$(COLOR_GREEN)✓ PostgreSQLが起動しました$(COLOR_RESET)"
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

# マイグレーション再実行用の初期化処理　(Re-initialize DB & wait for migrations)
db-fresh:
	@make db-clean
	@make db-up

## 初期データ投入

# SQLで開発用初期データを投入
init-data-sql:
	@echo "$(COLOR_BLUE)SQLで開発用初期データを投入中...$(COLOR_RESET)"
	docker compose -f $(COMPOSE_FILE) exec -T postgres psql -U loan_user -d loan_db < docs/dev-seed.sql
	@echo "$(COLOR_GREEN)✓ SQLでの初期データ投入が完了しました$(COLOR_RESET)"
