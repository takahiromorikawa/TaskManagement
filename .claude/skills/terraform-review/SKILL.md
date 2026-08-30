---
description: TaskManagementのTerraformコード（infra/配下）の品質チェックを行う。フロントエンド（oxlint）・バックエンド（Checkstyle）に相当する、Terraform版のlint/品質チェック。「Terraformの品質チェックをして」「infraをレビューして」のような依頼で使う。
---

# Terraformコード品質チェック（infra/）

TaskManagementプロジェクトの`infra/`配下のTerraformコードを品質チェックする際は、以下の手順・観点に従う。

## 1. 機械的チェック（必須・最優先）

```bash
cd infra
terraform fmt -check -recursive -diff   # フォーマット崩れがないか
terraform validate                       # 構文・参照エラーがないか
```

`terraform fmt`で差分が出た場合は`terraform fmt`（`-check`無し）を実行して修正を反映する。

`terraform plan`が安全に実行できる状況であれば、実行して意図しない差分（作成・変更・削除）が出ていないかも確認する
（`aws configure export-credentials --format env`でのcredential exportが必要な場合がある。詳細は[デプロイ手順の関連ドキュメント](../../../docs/infrastructure.md)を参照）。
`plan`は読み取り専用で安全だが、`apply`・`destroy`は必ずユーザーに確認してから実行する。

## 2. 手動レビューの観点

### 機密情報の混入がないこと

- `.tf`ファイル内にパスワード・APIキー・アクセスキーがハードコードされていないか
- `terraform.tfvars`・`*.pem`・`.terraform/`が`.gitignore`で除外されているか（`git status --ignored`で確認）
- `variable`ブロックで、パスワード等の機密値に`sensitive = true`が付与されているか

### 最小権限・ネットワークの公開範囲

- セキュリティグループの`cidr_blocks`が`0.0.0.0/0`になっている箇所を洗い出し、それが本当に全世界公開が必要なものか確認する
  （本プロジェクトは個人利用前提のため、SSH・HTTP・APIポートは自分のIP（`var.my_ip`）のみに制限する方針）
- RDS等のデータストアは`publicly_accessible = false`にし、セキュリティグループでも許可元をEC2のセキュリティグループなどに限定する（二重の制限）
- 不要なNAT Gateway・ALB・パブリックIPの割り当てがないか（コスト・攻撃面の両方に関わる）

### バージョン管理

- `required_providers`でプロバイダーのバージョンを固定しているか（`~>`等での範囲指定を含む）
- `.terraform.lock.hcl`がGit管理対象になっているか（`.terraform/`本体は対象外でよいが、lockファイルは再現性のためコミットする）

### 命名・タグ・可読性

- リソース名・`Name`タグが一貫した命名規則（例: `taskmanagement-*`）になっているか
- 変数に`description`が付与されているか
- コメントは「なぜそうしているか」（例: なぜNAT Gatewayを使わないか、なぜこのポートを開けるか）が必要な箇所にのみ付いているか

### 状態管理

- `user_data`など、変更してもリソースの実体が自動的に更新されない属性を変更する場合、`user_data_replace_on_change`のような
  明示的な再作成トリガーが必要かどうかを確認する

## 3. 報告形式

指摘事項は、フロントエンド・バックエンドのコードレビューと同様に「指摘内容・該当ファイル/行・修正案」の形式でまとめる。
機械的チェック（fmt/validate）はコマンド実行結果をそのまま報告し、手動レビューの観点は箇条書きで指摘する。

## 参照

インフラ構成の全体像は[インフラ構成](../../../docs/infrastructure.md)を参照。具体的な設定値やデプロイ手順の詳細は
このドキュメントには記載していないため、Terraformコード（`infra/`配下）とGitHub上のPR履歴を直接参照する。
このスキルは既存の`code-review`スキル（Java/TypeScript向け）を補完するTerraform専用のチェックリストである。
