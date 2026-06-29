# 🏷️貸出管理システム (Asset Loan Management System)

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Framework](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![CI](https://github.com/rohdfdk/asset-management/actions/workflows/ci.yaml/badge.svg)](https://github.com/rohdfdk/asset-management/actions)

社内の物品・資産貸出業務における「状態管理の属人化」と「不正状態の発生」を防止するため、
ドメイン駆動設計（DDD）に基づいて設計・実装したWebアプリケーションです。

---

## 📌 解決した課題

従来の貸出業務では以下の課題が存在していました：

- 貸出可否が担当者判断に依存
- 重複貸出や返却漏れの発生
- 状態遷移ルールが明文化されていない
- システム上で不正状態が発生可能

👉 これらを「状態遷移モデル」と「ドメイン制御」により解決

---

## 🏗️ 設計方針

本プロジェクトでは以下の設計思想を採用しています：

- 業務ルールをDomain層へ集約
- 状態遷移を明示的なモデルとして定義
- Entityの状態変更はDomain層でのみ制御
- UI・Application層では状態操作を禁止

👉 「状態の正しさ」をシステムで保証する設計

---

## 🔄 ドメインモデル（状態・業務ルール）

資産は以下の4状態で管理：

- AVAILABLE
- LOANED
- MAINTENANCE
- RETIRED

状態遷移はドメインルールとして制御され、
不正な遷移を防止しています。

👉 状態遷移図
- [資産貸出状態遷移図](docs/domain/state-transition-diagrams/asset-state-transition-diagram.mmd)

---

## 🧪 テスト・品質実績 ![Coverage](https://img.shields.io/badge/Entity_Coverage-100%25-brightgreen)

- Domain層を中心に単体テストを構築
- Domain Entity 層においてカバレッジ 100%
- ビジネスロジックの回帰防止を重視
- GitHub Actionsによる自動テスト

👉 「壊れないドメインモデル」を設計思想として採用

詳細なテストコードの記述ルールや各層のテスト方針については、[テスト方針・実績報告書](docs/testing/test-plan.md) を参照してください。

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

## 📸 画面イメージ 
<a id="demo"></a>
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

## 🛠️ 技術スタック

| 分類       | 技術・ツール                       | 状態 / 備考                         |
|:---------|:-----------------------------|:--------------------------------|
| Backend  | Java 21 / Spring Boot 3.5.14 | 主要ロジック実装                        |
| Build    | Maven                        | 依存関係管理 / Maven Wrapper (./mvnw) |
| Database | PostgreSQL                   | 開発環境: Docker Compose            |
| Quality  | JUnit 5 / AssertJ / JaCoCo   | 単体・結合テスト実行用                     |
| CI/CD    | GitHub Actions               | CI（自動テスト）構築                     |
| Infra    | Google Cloud (Cloud Run)     | 💡 本番環境として検討中                   |

---

## 📂 設計ドキュメント

本プロジェクトでは、コードを書く前の設計プロセスを重視し、ドメイン知識をドキュメントとして言語化・可視化しています。

* [ユビキタス言語定義集](docs/domain/ubiquitous-lexicon.md)
  資産貸出業務のドメイン知識を整理し、コードと認識を一致させるための用語集。
* [テスト方針・実績報告書](docs/testing/test-plan.md)
  テストピラミッドに基づく戦略、および品質実績のまとめ。

---

## 🏁 クイックスタート 
<a id="quickstart"></a>

本プロジェクトは、セキュリティ担保のため環境変数ファイル（`.env`, `application-local.yaml`）の設定が必要です。

```bash
# 1. リポジトリのクローンと移動
git clone https://github.com/rohdfdk/asset-management.git
cd asset-management

# 2. 環境変数の作成とDB起動
cp .env.example .env
make db-up

# 3. アプリケーションの起動
./mvnw spring-boot:run
```

⚙️ ステップごとの詳細・Windowsでの起動について
パスワードの設定値や、Windows（mvnw.cmd）での起動手順については、👉 **[ローカル環境起動ガイド](docs/local-setup.md)** を参照してください。

---