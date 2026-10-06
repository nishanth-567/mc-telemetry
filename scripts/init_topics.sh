#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
cp -n infra/.env.example infra/.env 2>/dev/null || true
docker compose -f infra/docker-compose.yml --env-file infra/.env up -d kafka kafka-init influxdb telegraf grafana kafka-ui
