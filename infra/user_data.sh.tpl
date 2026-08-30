#!/bin/bash
set -eux

dnf update -y
dnf install -y docker git

systemctl enable docker
systemctl start docker

# docker compose v2 プラグインを導入（Amazon Linux 2023の標準リポジトリには無いため直接配置する）
mkdir -p /usr/libexec/docker/cli-plugins
curl -SL "https://github.com/docker/compose/releases/download/v2.29.7/docker-compose-linux-x86_64" \
  -o /usr/libexec/docker/cli-plugins/docker-compose
chmod +x /usr/libexec/docker/cli-plugins/docker-compose

usermod -aG docker ec2-user

# IMDSv2でEC2自身のパブリックIPを取得する
TOKEN=$(curl -sX PUT "http://169.254.169.254/latest/api/token" -H "X-aws-ec2-metadata-token-ttl-seconds: 21600")
PUBLIC_IP=$(curl -sH "X-aws-ec2-metadata-token: $TOKEN" http://169.254.169.254/latest/meta-data/public-ipv4)

su - ec2-user -c "git clone --branch '${git_branch}' '${github_repo_url}' /home/ec2-user/app"

cat > /home/ec2-user/app/.env <<EOF
PUBLIC_IP=$PUBLIC_IP
DB_HOST=${db_host}
DB_USERNAME=${db_username}
DB_PASSWORD=${db_password}
EOF
chown ec2-user:ec2-user /home/ec2-user/app/.env

cd /home/ec2-user/app
docker compose -f docker-compose.prod.yml up -d --build
