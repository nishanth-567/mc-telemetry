# Workspace integration review

## Result

The submitted workspace had three concrete integration problems: Telegraf used hardcoded Influx credentials and names that did not match Docker Compose; Grafana used a separate hardcoded token; and the published `swarm.js` only launched one bot even though the package exposed baseline, ramp, and burst commands. The quickstart also referenced `infra/.env.example`, which was absent.

Those issues are fixed in the reviewed workspace. Telegraf now reads `INFLUX_TOKEN`, `INFLUX_ORG`, and `INFLUX_BUCKET` from its Compose environment. Grafana receives the same values and uses Grafana environment interpolation in its provisioned datasource. The bot runner now implements all three scenarios, supports environment overrides, disables physics as intended, cleans up chat timers, reconnects individual bots, and stops on duration or signal. The smoke test now supplies the Influx organization, sends Flux in the request body, and retries ingestion for up to 30 seconds. A checked-in `infra/.env.example` makes the documented bootstrap reproducible.

## Validation completed

- `node --check bots/swarm.js`: passed.
- `npm ci --ignore-scripts --prefix bots`: passed against the committed lockfile.
- `bash -n` passed for all shell scripts.
- Python compilation passed for `benchmark` and `spark`.
- Maven plugin packaging passed.
- YAML parsing and cross-file invariants passed with PyYAML.
- Bot invalid-scenario handling and bounded shutdown were exercised without a live Minecraft server.

## Not executable in this sandbox

The sandbox does not have Docker or a Spark runtime, so the actual Compose startup, Telegraf-to-Influx ingestion, Grafana provisioning, Paper connection, and Spark streaming run remain host-side gates. Run the following on a machine with Docker Desktop/Engine:

```bash
cp infra/.env.example infra/.env
# change local passwords/tokens in infra/.env
docker compose -f infra/docker-compose.yml --env-file infra/.env up -d
./scripts/smoke_test.sh
```

Then build and install the plugin, start the Paper 1.21.4 server, and run one bot scenario:

```bash
(cd plugin && mvn package)
(cd bots && npm ci && DURATION_SECONDS=60 npm run baseline)
```

Open Grafana at `http://localhost:3000` and Kafka UI at `http://localhost:8080`. Do not use the submitted `infra/.env` values outside a local demo; they are development credentials.
