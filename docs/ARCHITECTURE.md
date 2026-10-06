# Architecture

Paper event handlers serialize with Gson and use `offer()` into a bounded queue. A background thread owns Kafka I/O. This preserves the Minecraft main-thread constraint; full queues drop events and increment a counter. Kafka provides durable, replayable at-least-once transport. Telegraf converts JSON to InfluxDB points, while Spark reads the raw topic and produces windowed metrics.

Three partitions are enough for a laptop demo and permit parallel consumers without introducing a multi-node cluster. No TLS/authentication is in scope.
