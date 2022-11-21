with open('basic.rec', encoding="utf-8") as f:
  ln = 0
  lc = -1
  failed = False
  for l in f:
    lc+=1
    if lc < 2:
      continue
    n = int(l.split(':')[0])
    if n < ln:
      print('Line '+str(lc+1)+' has bad time value!')
      failed = True
    ln = n
  if failed:
    print('Input listing has errors!')
    exit(1)
