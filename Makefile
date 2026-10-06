up:
	docker compose -f infra/docker-compose.yml --env-file infra/.env up -d
down:
	docker compose -f infra/docker-compose.yml --env-file infra/.env down
reset:
	docker compose -f infra/docker-compose.yml --env-file infra/.env down -v
	scripts/init_topics.sh
smoke:
	scripts/smoke_test.sh
plugin:
	cd plugin && mvn package
bots:
	cd bots && npm ci && npm run baseline
spark:
	scripts/run_spark.sh
bench:
	python3 benchmark/harness.py --scenario baseline --duration 60
