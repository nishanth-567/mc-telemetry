import argparse,csv,time,subprocess,os
from pathlib import Path
ap=argparse.ArgumentParser();ap.add_argument('--scenario',choices=['baseline','ramp','burst'],required=True);ap.add_argument('--duration',type=int,default=60);a=ap.parse_args();out=Path('benchmark/results');out.mkdir(parents=True,exist_ok=True);p=subprocess.Popen(['node','bots/swarm.js',a.scenario],env={**os.environ,'DURATION_SECONDS':str(a.duration)});rows=[];start=time.time()
try:
 while time.time()-start<a.duration:
  rows.append({'second':int(time.time()-start),'scenario':a.scenario,'events_per_sec':'UNMEASURED','consumer_lag':'UNMEASURED','server_tps':'UNMEASURED','dropped_events':'UNMEASURED','e2e_latency_ms':'UNMEASURED'});time.sleep(1)
finally: p.terminate()
with open(out/f'{a.scenario}.csv','w',newline='') as f: w=csv.DictWriter(f,fieldnames=rows[0]);w.writeheader();w.writerows(rows)
print(f'Wrote {out}/{a.scenario}.csv; metrics requiring live instrumentation remain UNMEASURED.')
