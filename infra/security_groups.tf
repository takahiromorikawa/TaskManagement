resource "aws_security_group" "ec2" {
  name        = "taskmanagement-ec2-sg"
  description = "TaskManagement EC2: SSH/HTTP/backend API from my IP only"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "SSH (my IP only)"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.my_ip]
  }

  ingress {
    description = "Frontend (HTTP, my IP only)"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = [var.my_ip]
  }

  ingress {
    description = "Backend API (my IP only)"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = [var.my_ip]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "taskmanagement-ec2-sg"
  }
}

resource "aws_security_group" "rds" {
  name        = "taskmanagement-rds-sg"
  description = "TaskManagement RDS: allow PostgreSQL from the EC2 security group only"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "PostgreSQL (from EC2 security group only)"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.ec2.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "taskmanagement-rds-sg"
  }
}
