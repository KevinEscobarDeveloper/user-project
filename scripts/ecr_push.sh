#!/usr/bin/env bash
set -euo pipefail

APP="user-project"
REGION="${1:-us-east-1}"

AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_URI="$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$APP"

echo "[1/4] Creando repo ECR (si no existe)..."
aws ecr describe-repositories --repository-names "$APP" --region "$REGION" >/dev/null 2>&1 \
  || aws ecr create-repository --repository-name "$APP" --region "$REGION" >/dev/null

echo "[2/4] Login a ECR..."
aws ecr get-login-password --region "$REGION" \
 | docker login --username AWS --password-stdin "$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com"

echo "[3/4] Build imagen..."
docker build -f docker/Dockerfile -t "$APP:latest" .

echo "[4/4] Tag & push..."
docker tag "$APP:latest" "$ECR_URI:latest"
docker push "$ECR_URI:latest"

echo
echo "Listo. Imagen en: $ECR_URI:latest"
