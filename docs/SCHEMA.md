# Event and metric schema

Events are flat JSON. `ts` is epoch milliseconds. Tags in Influx: `type`, `player`, `world`, `detail` where low-cardinality. Coordinates and chat text are fields, never tags. This avoids exploding series cardinality from positions and arbitrary messages.

Metric records use `{ts,type,world,player,ore,count,value,chunk_x,chunk_z,active_players,flagged,ratio}` with absent values omitted by JSON serialization.
