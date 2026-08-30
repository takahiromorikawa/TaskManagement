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
