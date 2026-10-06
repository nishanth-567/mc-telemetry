# Pinned versions

| Component | Pin | Basis / note |
|---|---|---|
| Minecraft/Paper | 1.21.4 / Paper API 1.21.4-R0.1-SNAPSHOT | Selected for Java 21 and Mineflayer compatibility; runtime download still needs Gate 2 verification. |
| Java | 21 | Required by Paper 1.21.x and supported by Spark. |
| Kafka | Apache Kafka 4.0.0 Docker | KRaft single-node image. |
| InfluxDB | 2.7.12 | InfluxDB 2.x setup API. |
| Telegraf | 1.36.3 | json_v2 and kafka_consumer. |
| Grafana | 13.1.1 | Flux datasource. |
| Spark/PySpark | 4.2.0 | Official docs state Java 17/21/25, Python 3.10+, Scala 2.13. |
| Spark Kafka connector | spark-sql-kafka-0-10_2.13:4.2.0 | Official Spark integration coordinates. |
| Node | >=18 | Mineflayer README requirement. |
| Mineflayer | 4.31.0 | Supports Minecraft 1.21 family per official README. |
| Maven compiler | 3.13.0 | Pinned build plugin. |
| Maven shade | 3.6.0 | Pinned relocation plugin. |
