# 貸出管理システム (Asset Loan Management System)

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Framework](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![CI](https://github.com/rohdfdk/asset-management/actions/workflows/ci.yaml/badge.svg)](https://github.com/rohdfdk/asset-management/actions)

社内物品や書籍などの貸出・返却業務を効率化するためのバックエンドシステムです。
ドメイン駆動設計（DDD）の思想を取り入れ、ビジネスルールの堅牢性と変更への強さを意識して開発しています。

⚠️ Note: 本プロジェクトは開発中のため、機能やCIワークフローの仕様は予告なく変更される場合があります。

---

## 🏁 クイックスタート

本プロジェクトは、セキュリティ担保のため環境変数ファイル（`.env`, `application-local.yaml`）の設定が必要です。

```bash
# 1. リポジトリのクローンと移動
git clone https://github.com/rohdfdk/asset-management.git
cd my-app

# 2. 環境変数の作成とDB起動
cp .env.example .env
make db-up

# 3. アプリケーションの起動
./mvnw spring-boot:run
```

⚙️ ステップごとの詳細・Windowsでの起動について
パスワードの設定値や、Windows（mvnw.cmd）での起動手順については、👉 **[ローカル環境起動ガイド](docs/local-setup.md)** を参照してください。

---

## 🛠️ 技術スタック

| 分類       | 技術・ツール                       | 状態 / 備考                         |
|:---------|:-----------------------------|:--------------------------------|
| Backend  | Java 21 / Spring Boot 3.5.14 | 主要ロジック実装                        |
| Build    | Maven                        | 依存関係管理 / Maven Wrapper (./mvnw) |
| Database | PostgreSQL                   | 開発環境: Docker Compose            |
| Quality  | JUnit 5 / AssertJ / JaCoCo   | 単体・結合テスト実行用                     |
| CI/CD    | GitHub Actions               | ⚠️ 近日期限でCI（自動テスト）構築予定           |
| Infra    | Google Cloud (Cloud Run)     | 💡 本番環境として検討中                   |
---

## 📸 画面イメージ（マルチユーザー・認可制御）
ロール（管理者 / 一般）に応じたメニューの出し分けと、業務状況を可視化するダッシュボードを実装しています。

### 1. 管理者用画面（フルアクセス権限）
* **特徴:** 全資産・全ユーザーの管理権限、およびシステム全体の稼働状況を集計するサマリーバッジを表示。

<table>
  <tr>
    <td><img src="docs/design/screen-flow/dashboard-admin.png" width="350" alt="管理者画面"><br><sub>ダッシュボード</sub></td>
    <td><img src="docs/design/screen-flow/loannew-admin.png" width="350" alt="貸出申請画面"><br><sub>貸出申請画面</sub></td>
  </tr>
</table>

<details>
  <summary>🔍 他の管理者用画面（一覧系・2枚）を表示</summary>
  <br>
  <table>
    <tr>
      <td><img src="docs/design/screen-flow/assetlist-admin.png" width="350" alt="資産一覧画面"><br><sub>資産一覧画面</sub></td>
      <td><img src="docs/design/screen-flow/userlist-admin.png" width="350" alt="ユーザー一覧画面"><br><sub>ユーザー一覧画面</sub></td>
    </tr>
  </table>
</details>

---

### 2. 一般ユーザー用画面（最小権限の原則）
* **特徴:** 管理用メニューや機密項目を自動で非表示にし、自身の貸出・返却リクエストに特化したUI。

<table>
  <tr>
    <td><img src="docs/design/screen-flow/dashboard-user.png" width="350" alt="一般ユーザー画面（ダッシュボード）"><br><sub>ダッシュボード</sub></td>
    <td><img src="docs/design/screen-flow/loannew-user.png" width="350" alt="貸出申請画面"><br><sub>貸出申請画面</sub></td>
  </tr>
</table>

<details>
  <summary>🔍 一般ユーザー用の履歴画面を表示</summary>
  <br>
  <table>
    <tr>
      <td><img src="docs/design/screen-flow/loanlist-user.png" width="350" alt="貸出一覧画面"><br><sub>自分の貸出一覧</sub></td>
      <td></td> </tr>
  </table>
</details>

---

### 🗺️ 画面遷移図・認可コントロール
本アプリケーションの画面遷移と、Role（権限）によるアクセス制御の定義です。
```mermaid
stateDiagram-v2
    [*] --> Dashboard

    Dashboard --> AssetList: 資産一覧
    Dashboard --> UserList: ユーザー一覧
    Dashboard --> LoanList: 貸出一覧
    Dashboard --> LoanNew: 貸出登録

    AssetList --> Dashboard: 戻る
    UserList --> Dashboard: 戻る
    LoanList --> Dashboard: 戻る

    LoanList --> LoanNew: 貸出登録
    LoanNew --> LoanList: 登録
    LoanNew --> LoanList: キャンセル

    LoanList --> LoanList: 返却処理

    %% 各状態（画面）に対するビジネスルールの注記（NOTE）
    note right of UserList: 🔒 管理者 (ROLE_ADMIN)<br/>一般ユーザーには非表示
    note left of LoanList: 👤 データのアクセス制御<br/>・管理者は全件閲覧<br/>・一般は自身の貸出のみ
    note right of LoanNew: 🛡️ 安全なバリデーション<br/>ログイン中の本人名義のみ<br/>登録が許可される
```
---

## 🚀 本プロジェクトのこだわりポイント

* **DDDによる堅牢な設計**
    * 複雑な業務ルール（貸出可否・ペナルティ等）をドメイン層（Entity）へ完全にカプセル化。
    * **成果:** 仕様変更に強い設計を実現し、Domain層のブランチカバレッジ（C1）100%を達成。
* **Java 21を活用したガード句**
    * Pattern Matching for switch などのモダンな構文を積極採用。
    * **成果:** 不正データや異常な状態遷移をドメインの入り口で確実にブロックし、可読性と安全性を両立。
* **二重送信を防ぐべき等性の担保**
    * ボタン連打等のWebトラブルに備え、資産の返却処理等に「べき等」な状態遷移を実装。
* **【Next Step】CIによる品質の自動検証**
    * 開発スピード向上に向け、GitHub Actionsを用いたCI環境の構築を予定（自動テスト・JaCoCoによるカバレッジチェックの自動化）。

---

## 📂 設計ドキュメント

本プロジェクトでは、コードを書く前の設計プロセスを重視し、ドメイン知識をドキュメントとして言語化・可視化しています。

* [ユビキタス言語定義集](docs/domain/ubiquitous-lexicon.md)
  資産貸出業務のドメイン知識を整理し、コードと認識を一致させるための用語集。
* [テスト方針・実績報告書](docs/testing/test-plan.md)
  テストピラミッドに基づく戦略、および品質実績のまとめ。

### 🔄 状態遷移図
本アプリケーションにおける、主要な状態遷移の定義です。

* [資産貸出状態遷移図](docs/domain/state-transition-diagrams/asset-state-transition-diagram.mmd)
  業務の核となる、厳格な状態制御を可視化したメインの遷移図です。
* その他の各エンティティに関する詳細は[こちら](docs/domain/state-transition-diagrams/)

---

## 📊 テスト・品質実績

本プロジェクトでは「堅牢なドメインモデルの構築」を最優先とし、ビジネスロジックの核となる Domain 層から徹底的にテストを拡充しています。

* **現在のステータス:** PASS (100%) ｜ ![Coverage](https://img.shields.io/badge/Entity_Coverage-100%25-brightgreen)

現在は Domain Entity 層においてカバレッジ 100% を達成しており、他レイヤーのテストも順次拡大予定です。詳細なテストコードの記述ルールや各層のテスト方針については、[テスト方針・実績報告書](docs/testing/test-plan.md) を参照してください。

---