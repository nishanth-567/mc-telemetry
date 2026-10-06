import sys,pandas as pd,matplotlib.pyplot as plt
p=pd.read_csv(sys.argv[1]); ax=p.plot(x='second',y='events_per_sec',title='Minecraft telemetry benchmark'); ax.get_figure().savefig(sys.argv[2])
