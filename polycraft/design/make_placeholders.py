"""Generates PolyCraft's placeholder GUI textures. Run with `python make_placeholders.py` from this folder.

These are deliberately plain so they read as temporary. The maintainer's own art replaces them with files of the same
name and size (see the "UI art" section of DESIGN.md). Only textures the maintainer hasn't drawn yet are made here.
"""
import struct
import zlib
from pathlib import Path

OUT = Path(__file__).resolve().parent.parent / "common" / "src" / "main" / "resources" / "assets" / "polycraft" / "textures" / "gui"

CLEAR = (0, 0, 0, 0)
EDGE = (0x2B, 0x2B, 0x2B, 0xFF)
KEYHOLE = (0x2B, 0x2B, 0x2B, 0xFF)

# '.' is transparent, '#' the outline, 'b' the body, 'k' the keyhole. 12 x 12.
CLOSED = [
    "....####....",
    "...#....#...",
    "...#....#...",
    "...#....#...",
    "..########..",
    "..#bbbbbb#..",
    "..#bbbbbb#..",
    "..#bbkbbb#..",
    "..#bbkbbb#..",
    "..#bbbbbb#..",
    "..########..",
    "............",
]

OPEN = [
    "....####....",
    "...#....#...",
    "...#........",
    "...#........",
    "..########..",
    "..#bbbbbb#..",
    "..#bbbbbb#..",
    "..#bbkbbb#..",
    "..#bbkbbb#..",
    "..#bbbbbb#..",
    "..########..",
    "............",
]


def png(width, height, pixels):
    raw = b"".join(b"\x00" + b"".join(bytes(p) for p in row) for row in pixels)

    def chunk(kind, data):
        body = kind + data
        return struct.pack(">I", len(data)) + body + struct.pack(">I", zlib.crc32(body) & 0xFFFFFFFF)

    header = struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0)
    return b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", header) + chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b"")


def render(art, body):
    palette = {".": CLEAR, "#": EDGE, "b": body, "k": KEYHOLE}
    return [[palette[ch] for ch in row] for row in art]


def write(name, art, body):
    (OUT / name).write_bytes(png(12, 12, render(art, body)))
    print("wrote", OUT / name)


if __name__ == "__main__":
    OUT.mkdir(parents=True, exist_ok=True)
    write("lock_toggle.png", OPEN, (0x9A, 0x9A, 0x9A, 0xFF))
    write("lock_toggle_on.png", CLOSED, (0xE0, 0xA8, 0x20, 0xFF))
