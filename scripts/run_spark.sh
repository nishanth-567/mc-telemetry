#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../spark"
python3 -m pip install -r requirements.txt
spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.13:4.2.0 analytics.py --bootstrap "${KAFKA_BOOTSTRAP:-localhost:9092}"
