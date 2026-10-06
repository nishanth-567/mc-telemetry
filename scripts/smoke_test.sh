#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
source infra/.env

json='{"ts":1759400000123,"type":"block_break","player":"Smoke","uuid":"smoke","world":"world","x":120,"y":12,"z":-45,"detail":"DIAMOND_ORE"}'
metric='{"ts":1759400000123,"type":"ore_rate","world":"world","ore":"DIAMOND_ORE","count":3}'
printf '%s\n' "$json" | MSYS_NO_PATHCONV=1 docker exec -i mc-kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic minecraft-events >/dev/null
printf '%s\n' "$metric" | MSYS_NO_PATHCONV=1 docker exec -i mc-kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic minecraft-metrics >/dev/null

query() {
  local measurement="$1"
  curl -fsS "http://localhost:8086/api/v2/query?org=${INFLUXDB_INIT_ORG}" \
    -H "Authorization: Token ${INFLUXDB_INIT_ADMIN_TOKEN}" \
    -H 'Content-Type: application/vnd.flux' \
    --data-binary "from(bucket: \"${INFLUXDB_INIT_BUCKET}\") |> range(start: 0) |> filter(fn: (r) => r._measurement == \"${measurement}\") |> limit(n: 20)"
}

for attempt in $(seq 1 30); do
  event_rows="$(query mc_event 2>/dev/null || true)"
  metric_rows="$(query mc_metric 2>/dev/null || true)"
  if grep -q 'Smoke' <<<"$event_rows" && grep -q 'DIAMOND_ORE' <<<"$metric_rows"; then
    echo 'Gate 1 smoke test passed.'
    exit 0
  fi
  sleep 1
done

echo 'Smoke test failed: Kafka accepted records, but Telegraf/InfluxDB did not expose both measurements within 30 seconds.' >&2
exit 1
