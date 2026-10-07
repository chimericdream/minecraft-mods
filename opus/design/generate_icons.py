# Generates the Opus icon candidates (pixel art, 32x32 grid scaled 16x) into design/icon-candidates/.
# Requires Pillow: python design/generate_icons.py
from PIL import Image
import os

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'icon-candidates')
N, S = 32, 16  # 32x32 art grid, 16x upscale -> 512x512


def hexc(h, a=255):
    h = h.lstrip('#')
    return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4)) + (a,)


class Canvas:
    def __init__(self):
        self.im = Image.new('RGBA', (N, N), (0, 0, 0, 0))
        self.px = self.im.load()

    def p(self, x, y, c):
        if 0 <= x < N and 0 <= y < N:
            self.px[x, y] = hexc(c)

    def rect(self, x0, y0, x1, y1, c):  # inclusive
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                self.p(x, y, c)

    def glyph(self, x, y, rows, c):
        for dy, row in enumerate(rows):
            for dx, ch in enumerate(row):
                if ch == '#':
                    self.p(x + dx, y + dy, c)

    def save(self, name):
        self.im.resize((N * S, N * S), Image.NEAREST).save(os.path.join(OUT, name))


HASH = ["..#.#..", "#######", "..#.#..", "..#.#..", "#######", "..#.#..", "..#.#.."]
M = ["#...#", "##.##", "#.#.#", "#...#", "#...#"]
ARROW = ["..#..", "..#..", "#.#.#", ".###.", "..#.."]


def closed_tome():
    c = Canvas()
    OL, RED, RED_D, RED_L = '#1e0f10', '#9b2a2e', '#6e1c20', '#c4474a'
    GOLD, GOLD_D, PAGE, PAGE_D = '#f2c64b', '#b98a1e', '#f4ead0', '#cdbf9a'
    # outline + pages block (right + bottom edge visible)
    c.rect(5, 2, 27, 29, OL)
    c.rect(26, 4, 26, 27, PAGE)
    c.rect(26, 5, 26, 27, PAGE)
    for y in range(6, 27, 3):
        c.p(26, y, PAGE_D)
    c.rect(7, 28, 25, 28, PAGE_D)
    # cover
    c.rect(6, 3, 25, 27, RED)
    c.rect(6, 3, 25, 3, RED_L)
    c.rect(6, 3, 6, 27, RED_L)
    c.rect(6, 27, 25, 27, RED_D)
    c.rect(25, 3, 25, 27, RED_D)
    # spine
    c.rect(6, 3, 9, 27, RED_D)
    c.rect(9, 3, 9, 27, OL)
    for y in (6, 24):
        c.rect(6, y, 9, y, GOLD_D)
    # gold frame
    c.rect(12, 6, 23, 6, GOLD)
    c.rect(12, 24, 23, 24, GOLD)
    c.rect(12, 6, 12, 24, GOLD)
    c.rect(23, 6, 23, 24, GOLD)
    c.rect(12, 24, 23, 24, GOLD_D)
    c.rect(23, 6, 23, 24, GOLD_D)
    c.rect(12, 6, 23, 6, GOLD)
    c.rect(12, 6, 12, 24, GOLD)
    # '#' emblem
    c.glyph(14, 12, HASH, GOLD)
    # ribbon bookmark
    c.rect(20, 28, 21, 31, '#3b6fd0')
    c.p(20, 31, '#2a4f9a')
    c.save('icon-a-closed-tome.png')


def open_book():
    c = Canvas()
    OL = '#1e1410'
    COV, COV_D = '#7a4a26', '#55311a'
    PG, PG_D, PG_S = '#f6efd8', '#d9ccaa', '#c2b48c'
    INK, INK_L = '#2b2b33', '#8a8576'
    GOLD, GOLD_D = '#f2c64b', '#b98a1e'
    # cover base (visible as border)
    c.rect(1, 6, 30, 27, OL)
    c.rect(2, 7, 29, 26, COV)
    c.rect(2, 26, 29, 26, COV_D)
    # pages, with curved top (centre dips)
    for side in (0, 1):
        x0, x1 = (3, 15) if side == 0 else (16, 28)
        for x in range(x0, x1 + 1):
            d = abs(x - (15.5 if side == 0 else 16.5))
            top = 5 + int(d * 0.18)
            c.rect(x, top, x, 24, PG)
    c.rect(3, 24, 28, 24, PG_D)
    c.rect(3, 25, 28, 25, PG_S)
    # gutter shadow
    for y in range(6, 25):
        c.p(15, y, PG_D)
        c.p(16, y, PG_D)
    c.p(15, 5, PG_D); c.p(16, 5, PG_D)
    # outline top of pages
    for x in range(3, 29):
        d = abs(x - (15.5 if x <= 15 else 16.5))
        top = 5 + int(d * 0.18)
        c.p(x, top - 1, OL)
    c.p(15, 4, OL); c.p(16, 4, OL)
    # left page: heading + text lines
    c.glyph(5, 8, HASH, INK)
    c.rect(13, 10, 13, 10, PG)
    c.rect(5, 17, 13, 17, INK_L)
    c.rect(5, 19, 12, 19, INK_L)
    c.rect(5, 21, 13, 21, INK_L)
    c.rect(5, 23, 9, 23, INK_L)
    # heading underline (right of #)
    c.rect(14 - 4, 11, 13, 11, PG)
    c.rect(13, 9, 13, 9, PG)
    # right page: heading text bar + 3x3 recipe grid + lines
    c.rect(18, 8, 26, 9, INK)
    for gy in range(3):
        for gx in range(3):
            x, y = 20 + gx * 3, 12 + gy * 3
            c.rect(x, y, x + 1, y + 1, "#9a9585")
            c.p(x, y, '#b9b3a0')
    # arrow + result
    c.rect(23, 15, 24, 16, GOLD)
    c.rect(18, 22, 27, 22, INK_L)
    c.rect(18, 24, 24, 24, INK_L)
    c.save('icon-b-open-book.png')


def markdown_book():
    c = Canvas()
    OL = '#150f1a'
    COV, COV_L, COV_D = '#3a2a6e', '#5a46a0', '#261a4d'
    GOLD, GOLD_D = '#f2c64b', '#b98a1e'
    PAGE, PAGE_D = '#f4ead0', '#cdbf9a'
    c.rect(4, 3, 28, 28, OL)
    c.rect(26, 5, 28, 26, PAGE)
    for y in range(7, 26, 3):
        c.rect(26, y, 28, y, PAGE_D)
    c.rect(5, 4, 26, 27, COV)
    c.rect(5, 4, 26, 4, COV_L)
    c.rect(5, 4, 5, 27, COV_L)
    c.rect(5, 27, 26, 27, COV_D)
    c.rect(26, 4, 26, 27, COV_D)
    c.rect(5, 4, 8, 27, COV_D)
    c.rect(8, 4, 8, 27, OL)
    # gold plate with M-down-arrow
    c.rect(10, 9, 24, 21, GOLD_D)
    c.rect(10, 9, 23, 20, GOLD)
    c.rect(11, 10, 22, 19, COV_D)
    c.glyph(12, 12, M, GOLD)
    c.glyph(18, 12, ARROW, GOLD)
    # title strip
    c.rect(10, 24, 24, 24, GOLD_D)
    c.save('icon-c-markdown-tome.png')


if __name__ == '__main__':
    closed_tome()
    open_book()
    markdown_book()
