# Benchmarks

Run `python3 benchmark/harness.py --scenario baseline --duration 60`, then repeat for `ramp` and `burst`. The current harness writes honest `UNMEASURED` placeholders for metrics that require a live instrumented stack; replace them only with captured values. Report host CPU/RAM, Kafka lag method, and event-ts-to-Influx query availability method.
