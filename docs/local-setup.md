### 1. リポジトリのクローンと移動
```bash
git clone [https://github.com/rohdfdk/asset-management.git](https://github.com/rohdfdk/asset-management.git)
cd asset-management
```
### 2. データベースの起動
本リポジトリはセキュリティ担保のため、DBのパスワードをソースコードに含めていません。
リポジトリ内にある .env.example を活用し、以下の手順でローカル環境用の環境変数ファイルを作成してから起動してください。

1. サンプルファイルをコピーして .env を作成します。
```bash
cp .env.example .env
```
2. 作成した .env を開き、任意のDBパスワードを設定します。
```bash
POSTGRES_PASSWORD=your_local_db_password # ← ローカル用のDBパスワードを入力
```
3. Docker Compose でデータベースを起動します。
```bash
make db-up
```
### 3. ローカル環境用の設定ファイル作成
本リポジトリはセキュリティ担保のため、パスワードのデフォルト値をソースコード（Git管理対象）に含めていません。
リポジトリ内に用意されている `application-local.yaml.example` を活用し、以下の手順でローカル専用の設定ファイルを作成してください。

1. サンプルファイルをコピーして `application-local.yaml` を作成します。
```bash
   cp src/main/resources/application-local.yaml.example src/main/resources/application-local.yaml
```
2. 作成した application-local.yaml を開き、任意のパスワードに書き換えます。
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/loan_db
    username: loan_user
    password: your_local_db_password    # ← ローカル用のDBパスワードを入力
 
app:
  seed:
    admin:
      username: admin
      password: your_local_admin_password # ← 初期管理者のローカル用パスワードを入力
```
### 4. アプリケーションの起動
```bash
# Windows (コマンドプロンプト / PowerShell)
mvnw.cmd spring-boot:run
# Linux / macOS
./mvnw spring-boot:run
```