resource "aws_db_subnet_group" "default" {
  name       = "taskmanagement-db-subnet-group"
  subnet_ids = [aws_subnet.public.id, aws_subnet.public2.id]

  tags = {
    Name = "taskmanagement-db-subnet-group"
  }
}

resource "aws_db_instance" "postgres" {
  identifier     = "taskmanagement-db"
  engine         = "postgres"
  engine_version = "16"

  # 無料枠対象（12ヶ月間、750時間/月・ストレージ20GBまで無料）
  instance_class      = var.db_instance_class
  allocated_storage   = 20
  storage_type        = "gp2"
  multi_az            = false
  publicly_accessible = false

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.default.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  # 学習用のため、削除時にスナップショットを取らず即座に削除できるようにする
  skip_final_snapshot = true
  deletion_protection = false

  tags = {
    Name = "taskmanagement-db"
  }
}
