import json
import urllib.request
import sys

url = 'https://files.minecraftforge.net/maven/net/minecraftforge/forge/promotions_slim.json'

req = urllib.request.Request(url)
try:
  with urllib.request.urlopen(req) as res:
    body = json.load(res)
  print(body["promos"][sys.argv[1]])
except Exception as e:
  exit(1)
