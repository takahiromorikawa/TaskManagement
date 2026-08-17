[« 要件定義書に戻る](requirements.md)

# 技術スタック：TaskManagement

## バックエンド

- 言語：Java 25
- フレームワーク：Spring Boot 4.1
- ビルドツール：Gradle
- データアクセス：Spring Data JPA
- DBマイグレーション：Flyway（PostgreSQLのスキーマをコードでバージョン管理する。Spring Bootと親和性が高い標準的な組み合わせ）
- テスト：JUnit 5（Spring Boot標準）
- ボイラープレート削減：Lombok

## データベース

- PostgreSQL
- ローカル開発ではDocker（docker compose）でのコンテナ起動を推奨し、H2などのインメモリDBは使用しない
- アプリケーションを再起動してもデータは失われない（H2インメモリ運用からの変更点。詳細は[非機能要件](requirements.md#非機能要件)を参照）

## フロントエンド

- フレームワーク：React（Next.jsは対象外とし、Reactのみのシンプルな構成とする）
- ビルドツール：Vite（Create React Appは非推奨のため、現行標準のViteを採用）
- パッケージマネージャー：npm
- APIとの通信：Fetch API（ブラウザ標準機能のみで完結させ、追加ライブラリは導入しない）

## フロントエンド・バックエンド間

- 別オリジン構成のため、バックエンド側でCORS設定を行う

## 確認方法

- React画面から動作確認する。API単体の確認にはPostman等も併用可能
