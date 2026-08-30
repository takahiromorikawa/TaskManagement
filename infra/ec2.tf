data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

resource "aws_key_pair" "deployer" {
  key_name   = "taskmanagement-deploy-key"
  public_key = file(var.ssh_public_key_path)
}

resource "aws_instance" "app" {
  ami                         = data.aws_ami.amazon_linux.id
  instance_type               = var.instance_type
  key_name                    = aws_key_pair.deployer.key_name
  subnet_id                   = aws_subnet.public.id
  vpc_security_group_ids      = [aws_security_group.ec2.id]
  associate_public_ip_address = true

  # Amazon Linux 2023のAMIは30GB以上が必須（無料枠の上限ちょうど）
  root_block_device {
    volume_size = 30
    volume_type = "gp3"
  }

  user_data = templatefile("${path.module}/user_data.sh.tpl", {
    github_repo_url = var.github_repo_url
    git_branch      = var.git_branch
  })

  tags = {
    Name = "taskmanagement-app"
  }
}
