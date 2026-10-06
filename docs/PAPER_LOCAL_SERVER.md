# Local Paper server setup

Gate 2 requires a Paper 1.21.4 server. Download the exact Paper 1.21.4 build from the official Paper downloads page, create a separate server directory, and copy `plugin/target/mc-telemetry-1.0.0.jar` into `plugins/`.

Create `server.properties` with:

```properties
server-port=25565
online-mode=false
view-distance=6
simulation-distance=4
level-type=flat
generate-structures=false
spawn-protection=0
max-players=60
```

`online-mode=false` is acceptable only for this local, offline test server so Mineflayer can connect without Microsoft accounts. Never expose this server publicly.

Start it once to accept the EULA, set `eula=true` in `eula.txt`, then start again:

```bash
java -Xms2G -Xmx4G -jar paper-1.21.4-*.jar --nogui
```

The Kafka bootstrap address depends on where Paper runs:

- Paper on the host: use `localhost:9092` in `plugin/config.yml`.
- Paper in the same Compose network: use `kafka:29092`.

The generated default plugin config uses `kafka:29092`, matching the Compose-network case. For a host-local Paper server, edit it before starting Paper.
