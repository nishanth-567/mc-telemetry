# Minecraft Server Real-Time Telemetry

A reproducible Big Data Analytics course project: Paper events → Kafka → Telegraf/InfluxDB/Grafana, with Spark Structured Streaming metrics and Mineflayer load.

## Prerequisites

Docker Desktop or Docker Engine with Compose, Java 21, Maven, Node.js >=18, Python 3.10+, and a Paper 1.21.4 server jar for Gate 2. See [docs/PAPER_LOCAL_SERVER.md](docs/PAPER_LOCAL_SERVER.md).

## Quickstart

```bash
git clone <repo> mc-telemetry && cd mc-telemetry
cp infra/.env.example infra/.env
# edit infra/.env and replace dev credentials
docker compose -f infra/docker-compose.yml --env-file infra/.env up -d
./scripts/smoke_test.sh
(cd plugin && mvn package)
(cd bots && npm ci)
```

Open Grafana at http://localhost:3000 (admin/admin from the example env) and Kafka UI at http://localhost:8080.

## Five-minute demo

```bash
cp infra/.env.example infra/.env
docker compose -f infra/docker-compose.yml --env-file infra/.env up -d
./scripts/smoke_test.sh
(cd plugin && mvn package)
# Start Paper 1.21.4 with plugin/target/mc-telemetry-1.0.0.jar in its plugins/ directory
(cd bots && npm ci && DURATION_SECONDS=240 npm run baseline)
# In another terminal
./scripts/run_spark.sh
# Visit http://localhost:3000 and show the provisioned dashboard
```

## Components

- `infra/`: Kafka KRaft, InfluxDB, Telegraf, Grafana, Kafka UI
- `plugin/`: non-blocking Paper plugin with bounded queue and shaded dependencies
- `bots/`: Mineflayer baseline/ramp/burst scenarios
- `spark/`: five windowed Structured Streaming jobs
- `benchmark/`: scenario harness and plotting

## Troubleshooting

- Kafka connection errors: use `kafka:29092` inside Compose and `localhost:9092` on the host.
- Plugin classloader errors: use the shaded jar and do not copy the unshaded original.
- Mineflayer mismatch: keep `MC_VERSION=1.21.4`; do not let the bot auto-detect a different server.
- Bot scenarios: `baseline` starts 5 bots, `ramp` starts 10 bots 15 seconds apart, and `burst` starts 10 bots immediately. Set `MC_HOST`, `MC_PORT`, `MC_VERSION`, `DURATION_SECONDS`, `BOT_COUNT`, or `BOT_PREFIX` to tune a local run.
- Influx wiring: Telegraf and Grafana read the same `INFLUXDB_INIT_*` values passed by Compose. Keep the example organization `mc` and bucket `minecraft` unless you also update the dashboard Flux queries.
- Spark connector mismatch: keep Spark and `spark-sql-kafka-0-10_2.13` at 4.2.0.
- Offline mode is only for the local test server; never use it for a public server.

See `docs/ASSUMPTIONS.md` for anything not verified in this environment.
