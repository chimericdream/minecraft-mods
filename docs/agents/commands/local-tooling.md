# Local tooling (Windows)

Verified 2026-10-17 on the primary dev machine. Use these directly instead of probing for what is
installed.

## Python

- **Use `python`** (Python 3.14, `C:\Python314\python.exe`). `py` also works (`C:\Windows\py.exe`).
- **Do NOT use `python3`** (the Microsoft Store stub; a Claude pre-tool hook blocks it).
- The Store stub `python` in `WindowsApps` is also on `PATH`, but the real install comes first in
  both Git Bash and PowerShell, so plain `python` is safe.
- When reading untrusted files, run `python -I script.py`.
- Writing files: see the file-writing rule in `conventions/code-style.md`.

## Shells

- Prefer **Bash** (Git Bash) for scripting. File writes from Windows PowerShell 5.1 are blocked by a
  Claude pre-tool hook (non-UTF-8 codepage); use the Write/Edit tools or Python.
- `PATH` is equivalent across both shells for `python`, `py` and `java`.

## Other tools on `PATH`

- `java`: JDK 26.0.1 at `C:\Users\bill\.lib\jdk-26.0.1`. The build targets Java 25 via Gradle
  (`options.release = 25`); don't assume the `PATH` JDK is the one Gradle uses.
- `git`, `node`: available in Bash.

## Checking for non-UTF-8 text files

`bun run lint` (see `conventions/code-style.md`). Known false positive handled there: `*.pdn`.
