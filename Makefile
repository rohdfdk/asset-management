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
	@echo "$(COLOR_BOLD)貸出管理アプリ - 開発用Makefile$(COLOR_RESET)"
	@echo ""
	@echo "$(COLOR_GREEN)Docker関連:$(COLOR_RESET)"
	@echo "  make db-up        - PostgreSQLコンテナを起動"
	@echo "  make db-down      - PostgreSQLコンテナを停止"
	@echo "  make db-logs      - PostgreSQLログを表示"
	@echo "  make db-clean     - PostgreSQLコンテナとボリュームを削除"
	@echo ""
	@echo "$(COLOR_GREEN)初期データ投入:$(COLOR_RESET)"
	@echo "  make init-data    - 全ての初期データを投入"
	@echo "  make init-users   - ユーザーデータを投入"
	@echo "  make init-assets  - 資産データを投入"
	@echo "  make init-loans   - 貸出データを投入"
	@echo ""
	@echo "$(COLOR_GREEN)API動作確認:$(COLOR_RESET)"
	@echo "  make test-api     - API動作確認"
	@echo ""

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

# 全ての初期データを投入
init-data: init-users init-assets init-loans
	@echo ""
	@echo "$(COLOR_GREEN)$(COLOR_BOLD)✓ 全ての初期データ投入が完了しました！$(COLOR_RESET)"
	@echo ""

# ユーザーデータを投入
init-users:
	@echo "$(COLOR_BLUE)ユーザーデータを投入中...$(COLOR_RESET)"
	@curl -s -X POST $(API_URL)/users \
		-H "Content-Type: application/json" \
		-d '{"username":"admin","password":"admin123","email":"admin@example.com","fullName":"管理者","role":"ADMIN"}' \
		| jq . || echo "Failed to create admin user"
	@curl -s -X POST $(API_URL)/users \
		-H "Content-Type: application/json" \
		-d '{"username":"tanaka","password":"tanaka123","email":"tanaka@example.com","fullName":"田中太郎","role":"USER"}' \
		| jq . || echo "Failed to create tanaka user"
	@curl -s -X POST $(API_URL)/users \
		-H "Content-Type: application/json" \
		-d '{"username":"suzuki","password":"suzuki123","email":"suzuki@example.com","fullName":"鈴木花子","role":"USER"}' \
		| jq . || echo "Failed to create suzuki user"
	@curl -s -X POST $(API_URL)/users \
		-H "Content-Type: application/json" \
		-d '{"username":"sato","password":"sato123","email":"sato@example.com","fullName":"佐藤次郎","role":"USER"}' \
		| jq . || echo "Failed to create sato user"
	@echo "$(COLOR_GREEN)✓ ユーザーデータの投入が完了しました$(COLOR_RESET)"

# 資産データを投入
init-assets:
	@echo "$(COLOR_BLUE)資産データを投入中...$(COLOR_RESET)"
	@curl -s -X POST $(API_URL)/assets \
		-H "Content-Type: application/json" \
		-d '{"assetCode":"PC001","name":"ノートPC ThinkPad X1","description":"開発用ノートPC Core i7/16GB RAM","category":"PC","location":"オフィス3F"}' \
		| jq . || echo "Failed to create PC001"
	@curl -s -X POST $(API_URL)/assets \
		-H "Content-Type: application/json" \
		-d '{"assetCode":"PC002","name":"ノートPC MacBook Pro","description":"デザイナー用ノートPC M2/32GB RAM","category":"PC","location":"オフィス3F"}' \
		| jq . || echo "Failed to create PC002"
	@curl -s -X POST $(API_URL)/assets \
		-H "Content-Type: application/json" \
		-d '{"assetCode":"PC003","name":"デスクトップPC","description":"検証用デスクトップPC","category":"PC","location":"サーバールーム"}' \
		| jq . || echo "Failed to create PC003"
	@curl -s -X POST $(API_URL)/assets \
		-H "Content-Type: application/json" \
		-d '{"assetCode":"MON001","name":"モニター Dell 27インチ","description":"4Kモニター 27インチ","category":"モニター","location":"オフィス3F"}' \
		| jq . || echo "Failed to create MON001"
	@curl -s -X POST $(API_URL)/assets \
		-H "Content-Type: application/json" \
		-d '{"assetCode":"MON002","name":"モニター LG 32インチ","description":"ウルトラワイドモニター","category":"モニター","location":"オフィス3F"}' \
		| jq . || echo "Failed to create MON002"
	@curl -s -X POST $(API_URL)/assets \
		-H "Content-Type: application/json" \
		-d '{"assetCode":"TAB001","name":"iPad Pro 12.9","description":"プレゼン用タブレット","category":"タブレット","location":"会議室A"}' \
		| jq . || echo "Failed to create TAB001"
	@curl -s -X POST $(API_URL)/assets \
		-H "Content-Type: application/json" \
		-d '{"assetCode":"CAM001","name":"Webカメラ Logicool","description":"Web会議用カメラ 4K対応","category":"周辺機器","location":"会議室B"}' \
		| jq . || echo "Failed to create CAM001"
	@curl -s -X POST $(API_URL)/assets \
		-H "Content-Type: application/json" \
		-d '{"assetCode":"KEY001","name":"メカニカルキーボード","description":"英語配列 青軸","category":"周辺機器","location":"倉庫"}' \
		| jq . || echo "Failed to create KEY001"
	@curl -s -X POST $(API_URL)/assets \
		-H "Content-Type: application/json" \
		-d '{"assetCode":"MOU001","name":"ワイヤレスマウス","description":"Logicool MX Master 3","category":"周辺機器","location":"倉庫"}' \
		| jq . || echo "Failed to create MOU001"
	@curl -s -X POST $(API_URL)/assets \
		-H "Content-Type: application/json" \
		-d '{"assetCode":"PRJ001","name":"プロジェクター Epson","description":"会議室用プロジェクター 4000lm","category":"プロジェクター","location":"会議室A"}' \
		| jq . || echo "Failed to create PRJ001"
	@echo "$(COLOR_GREEN)✓ 資産データの投入が完了しました$(COLOR_RESET)"

# 貸出データを投入
init-loans:
	@echo "$(COLOR_BLUE)貸出データを投入中...$(COLOR_RESET)"
	@curl -s -X POST $(API_URL)/loans \
		-H "Content-Type: application/json" \
		-d '{"assetId":1,"userId":2,"loanDate":"2026-05-20","expectedReturnDate":"2026-06-20","remarks":"新プロジェクト用"}' \
		| jq . || echo "Failed to create loan 1"
	@curl -s -X POST $(API_URL)/loans \
		-H "Content-Type: application/json" \
		-d '{"assetId":4,"userId":2,"loanDate":"2026-05-20","expectedReturnDate":"2026-06-20","remarks":"デュアルモニター環境構築"}' \
		| jq . || echo "Failed to create loan 2"
	@curl -s -X POST $(API_URL)/loans \
		-H "Content-Type: application/json" \
		-d '{"assetId":2,"userId":3,"loanDate":"2026-05-22","expectedReturnDate":"2026-07-22","remarks":"デザイン作業用"}' \
		| jq . || echo "Failed to create loan 3"
	@echo "$(COLOR_GREEN)✓ 貸出データの投入が完了しました$(COLOR_RESET)"

## API動作確認

# API動作確認
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