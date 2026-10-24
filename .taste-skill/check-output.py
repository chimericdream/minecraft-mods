from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import unquote, urlsplit
import json

root = Path('site/dist').resolve()

class Links(HTMLParser):
    def __init__(self):
        super().__init__()
        self.links = []

    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        for key in ('href', 'src'):
            if key in attrs:
                self.links.append(attrs[key])

missing, absolute, checked = [], [], 0
pages = list(root.rglob('*.html'))
for page in pages:
    parser = Links()
    parser.feed(page.read_text(encoding='utf-8'))
    for link in parser.links:
        url = urlsplit(link)
        if url.scheme or url.netloc or not url.path:
            continue
        if url.path.startswith('/'):
            absolute.append([str(page.relative_to(root)), link])
        target = page.parent / unquote(url.path)
        checked += 1
        if not target.is_file() and not (target / 'index.html').is_file():
            missing.append([str(page.relative_to(root)), link])

result = dict(pages=len(pages), checked=checked, missing=missing, absolute=absolute)
Path('.taste-skill/link-check.json').write_text(json.dumps(result, indent=2)+'\n', encoding='utf-8', newline='\n')
print(json.dumps(result, indent=2))
assert not missing and not absolute
