import math
from analytics import chunk_expr
def test_negative_chunks(): assert math.floor(-1/16)==-1 and math.floor(-16/16)==-1 and math.floor(-17/16)==-2
def test_suspicious_threshold(): assert 3/(3+5)>0.35 and not (1/(1+5)>0.35)
