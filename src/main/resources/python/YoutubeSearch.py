import sys
import json
from pytube import Search
from pytube import YouTube

if len(sys.argv) != 2:
    sys.exit(1)

search_string = sys.argv[1]

s = Search(search_string)

results_list = []

for v in s.results:
    result_info = {
        "title": v.title,
        "watch_url": v.watch_url,
        "thumbnail_url": YouTube(v.watch_url).thumbnail_url
    }
    results_list.append(result_info)

print(json.dumps(results_list))