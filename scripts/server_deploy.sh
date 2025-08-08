#!/usr/bin/env bash
set -euo pipefail

REGION="${1:-us-east-1}"

if grep -q '^ECR_IMAGE_URI=' .env; then
  echo "[1/3] Login a ECR en servidor..."
  AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
  aws ecr get-login-password --region "$REGION" \
    | docker login --username AWS --password-stdin "$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com"

  echo "[2/3] Pull de imagen..."
  docker compose --env-file .env pull app || true
fi

echo "[3/3] Up -d..."
docker compose --env-file .env up -d

echo "Despliegue listo. Contenedores:"
docker ps --format 'table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}'
