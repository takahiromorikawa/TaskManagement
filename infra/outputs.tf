output "public_ip" {
  description = "EC2インスタンスのパブリックIPアドレス"
  value       = aws_instance.app.public_ip
}

output "frontend_url" {
  description = "フロントエンド（React）のURL"
  value       = "http://${aws_instance.app.public_ip}"
}

output "backend_url" {
  description = "バックエンドAPIのURL"
  value       = "http://${aws_instance.app.public_ip}:8080/cards"
}

output "ssh_command" {
  description = "EC2へSSH接続するコマンド"
  value       = "ssh -i ~/.ssh/taskmanagement-aws ec2-user@${aws_instance.app.public_ip}"
}

output "rds_endpoint" {
  description = "RDSのエンドポイント（EC2からのみ接続可能）"
  value       = aws_db_instance.postgres.address
}
