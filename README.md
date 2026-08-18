# TaskManagement

カンバンボードUIでタスクを管理するアプリです。
Spring Boot（バックエンド）＋ React（フロントエンド）構成で構築しています。

背景・目的は[要件定義書](docs/requirements.md)を参照してください。

## 主な機能

- カードの新規作成（タイトル・優先度・期限）
- カンバンボード表示（未着手／進行中／完了の3列）
- カードの編集（タイトル・優先度・期限）
- ドラッグ&ドロップによるステータス変更・並び替え
- 並び替え条件の切り替え（追加順／期限順／優先度順）
- カード削除
- スマートフォン幅でのレスポンシブレイアウト

詳細は[機能要件](docs/features.md)を参照してください。

## 技術スタック

| 分類 | 技術 |
|---|---|
| バックエンド | Java 25 / Spring Boot 4.1.0 / Gradle 9.5.1 / Spring Data JPA / Flyway / Lombok |
| フロントエンド | React 19.2.8 / Vite 8.2.1 / TypeScript 6.0.3 |
| データベース | PostgreSQL 16 |

詳細は[技術スタック](docs/tech-stack.md)を参照してください。

## ドキュメント

| ドキュメント | 内容 |
|---|---|
| [要件定義書](docs/requirements.md) | 背景・目的、非機能要件 |
| [機能要件](docs/features.md) | 機能一覧、ユースケース・操作フロー |
| [画面仕様](docs/screens.md) | 画面構成、ワイヤーフレーム、画面遷移図 |
| [データベース設計](docs/database.md) | データ項目、ER図 |
| [API仕様](docs/api.md) | エンドポイント一覧、シーケンス図 |
| [技術スタック](docs/tech-stack.md) | バックエンド・フロントエンド・DBの技術構成 |

## セットアップ

### 前提

- Java 25
- Node.js（npm）
- Docker（PostgreSQLの起動に使用）

### 1. データベースの起動

```bash
docker compose up -d
```

PostgreSQLが `localhost:5432` で起動します（DB名: `taskdb` / ユーザー: `postgres` / パスワード: `postgres`）。
接続情報は[application.properties](src/main/resources/application.properties)を参照してください。

### 2. バックエンドの起動

```bash
./gradlew bootRun
```

`http://localhost:8080` でAPIサーバーが起動します。起動時にFlywayによるマイグレーションが自動実行されます。

### 3. フロントエンドの起動

```bash
cd frontend
npm install
npm run dev
```

`http://localhost:5173` で画面が表示されます。

> ポート（バックエンド8080／フロントエンド5173）が競合している場合は、占有プロセスを停止した上で必ずデフォルトポートで起動し直してください。代替ポートでの起動は行いません。

## API概要

| メソッド | URL | 内容 |
|---|---|---|
| GET | /cards | カード一覧取得 |
| GET | /cards/{id} | カード詳細取得 |
| POST | /cards | カード新規作成 |
| PUT | /cards/{id} | カード編集 |
| PUT | /cards/{id}/status | ステータス変更 |
| PUT | /cards/reorder | 並び替え |
| DELETE | /cards/{id} | カード削除 |

詳細は[API仕様](docs/api.md)を参照してください。

## 開発フロー

このリポジトリでは`main`ブランチへの直接pushを禁止しています。Issue作成・ブランチ作成・PR作成・マージのルールは[CLAUDE.md](CLAUDE.md)を参照してください。
