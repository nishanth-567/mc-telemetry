# Assumptions and verification status

- **UNVERIFIED:** Docker images may require adjustment if a tag is unavailable in the student's registry.
- **UNVERIFIED:** Paper runtime download and plugin loading require a local Docker run or local Paper jar; the source build is independently testable with Maven.
- Grafana datasource provisioning uses the dev token from `infra/.env`; this is intentionally local-only and must be changed outside a laptop demo.
- Chat is captured with Paper's `AsyncPlayerChatEvent`; it is safe for queue insertion because no Bukkit network/disk I/O occurs in the handler.
- Influx timestamps are event timestamps; Spark update-mode repeats a window-end timestamp and later writes overwrite the same series point.
- Benchmark end-to-end latency is **UNMEASURED** until Kafka offsets/Influx query sampling are wired to a running stack; no fabricated numbers are included.
- This sandbox has not yet completed a full Docker Gate 1-5 run; statuses must remain PARTIAL/UNVERIFIED until the exact commands are run on a host with Docker.
