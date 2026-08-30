variable "aws_region" {
  description = "デプロイ先のAWSリージョン"
  type        = string
  default     = "us-east-1"
}

variable "instance_type" {
  description = "EC2インスタンスタイプ（無料枠対象のt2.microを推奨）"
  type        = string
  default     = "t2.micro"
}

variable "db_instance_class" {
  description = "RDSインスタンスクラス（無料枠対象のdb.t4g.microを推奨）"
  type        = string
  default     = "db.t4g.micro"
}

variable "db_name" {
  description = "RDSに作成するデータベース名"
  type        = string
  default     = "taskdb"
}

variable "db_username" {
  description = "RDSのマスターユーザー名"
  type        = string
  default     = "postgres"
}

variable "db_password" {
  description = "RDSのマスターパスワード（terraform.tfvarsで指定する。Gitにコミットしないこと）"
  type        = string
  sensitive   = true
}

variable "my_ip" {
  description = "SSH接続を許可する自分のグローバルIPアドレス（例: 203.0.113.1/32）"
  type        = string
}

variable "ssh_public_key_path" {
  description = "EC2に登録する公開鍵ファイルのパス"
  type        = string
  default     = "~/.ssh/taskmanagement-aws.pub"
}

variable "github_repo_url" {
  description = "EC2上でcloneするGitHubリポジトリのURL"
  type        = string
  default     = "https://github.com/takahiromorikawa/TaskManagement.git"
}

variable "git_branch" {
  description = "cloneするブランチ"
  type        = string
  default     = "main"
}
