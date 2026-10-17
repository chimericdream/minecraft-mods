# Local tooling (Windows)

Verified 2026-10-17 on the primary dev machine. Use these directly instead of probing for what is
installed.

## Python

- **Use `python`** (Python 3.14, `C:\Python314\python.exe`). `py` also works (`C:\Windows\py.exe`).
- **Do NOT use `python3`.** It resolves to the Microsoft Store stub in
  `C:\Users\bill\AppData\Local\Microsoft\WindowsApps\`, which prints "Python was not found; run
  without arguments to install from the Microsoft Store" even though Python is installed.
- The Store stub `python` in `WindowsApps` is also on `PATH`, but the real install comes first in
  both Git Bash and PowerShell, so plain `python` is safe.
- When reading untrusted files, run `python -I script.py`.
- Writing files: pass `encoding="utf-8"` and `newline="\n"` to `open(...)` (see the LF rule in
  `conventions.md`).

## Shells

- Prefer **Bash** (Git Bash) for scripting. Do not write files with Windows PowerShell 5.1:
  `Set-Content`, `Out-File` and `>` default to a non-UTF-8 codepage and have produced invalid
  bytes in markdown files. Use the Write/Edit tools or Python instead.
- `PATH` is equivalent across both shells for `python`, `py` and `java`.

## Other tools on `PATH`

- `java`: JDK 26.0.1 at `C:\Users\bill\.lib\jdk-26.0.1`. The build targets Java 25 via Gradle
  (`options.release = 25`); don't assume the `PATH` JDK is the one Gradle uses.
- `git`, `node`: available in Bash.

## Checking for non-UTF-8 text files

```sh
git ls-files -z --cached --others --exclude-standard | python -I -c "
import sys
for p in sys.stdin.buffer.read().split(b'\0'):
    if not p: continue
    try: d = open(p, 'rb').read()
    except OSError: continue
    if b'\0' in d[:8192]: continue  # binary
    try: d.decode('utf-8')
    except UnicodeDecodeError as e: print(p.decode(errors='replace'), e.start)
"
```

Known false positives: `*.pdn` (Paint.NET sources).
