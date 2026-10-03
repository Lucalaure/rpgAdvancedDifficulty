import zlib, struct, math, sys

ART = [
"................................",
"................................",
"................................",
"........K......KK......K........",
".......KYK....KYYK....KYK.......",
".......KYYK..KYYYYK..KYYK.......",
".......KYYYKKKYYYYKKKYYYK.......",
".......KYYYYYYYYYYYYYYYYK.......",
".......KYGYYCYYGGYYCYYGYK.......",
".......KyyyyyyyyyyyyyyyyK.......",
"......KKKKKKKKKKKKKKKKKKKK......",
"......KWWWWWWWWWWWWWWWWWWK......",
".....KWWWWWWWWWWWWWWWWWWWWK.....",
".....KWWWWWWWWWWWWWWWWWWWWK.....",
".....KWWWKKKKWWWWWWKKKKWWWK.....",
".....KWWKrrrrKWWWWKrrrrKWWK.....",
".....KWWKrRRrKWWWWKrRRrKWWK.....",
".....KWWKrRRrKWWWWKrRRrKWWK.....",
".....KWWWKrrKWWKKWWKrrKWWWK.....",
".....KwWWWKKWWKKKKWWKKWWWwK.....",
".....KwwWWWWWWWKKWWWWWWWwwK.....",
"......KwwWWWWWWWWWWWWWWwwK......",
".......KwWKWKWKWWKWKWKWwK.......",
".......KwWKWKWKWWKWKWKWwK.......",
"........KKwwwwwwwwwwwwKK........",
"..........KKKKKKKKKKKK..........",
"................................",
"................................",
"...YY....YY....YY....YY....YY...",
"..YYYY..YYYY..YYYY..YYYY..YYYY..",
"...yy....yy....yy....yy....yy...",
"................................",
]
assert len(ART) == 32 and all(len(r) == 32 for r in ART), [len(r) for r in ART]
for r in ART:
    assert r == r[::-1], r  # symmetric

COLORS = {
    'K': (42, 22, 26), 'W': (236, 228, 210), 'w': (184, 172, 150),
    'R': (255, 70, 60), 'r': (170, 18, 24),
    'Y': (255, 207, 58), 'y': (200, 140, 26),
    'G': (226, 35, 58), 'C': (58, 209, 255),
}

def background(x, y):
    # Dark red glow behind the skull, fading to near-black at the corners
    d = math.hypot(x - 15.5, y - 16.5) / 22.0
    t = max(0.0, min(1.0, d))
    inner, outer = (122, 18, 34), (18, 8, 12)
    return tuple(round(inner[i] + (outer[i] - inner[i]) * t ** 0.8) for i in range(3))

def pixel(x, y):
    c = ART[y][x]
    if c in COLORS:
        # Soft glow around the eyes
        return COLORS[c]
    bg = background(x, y)
    for ex in (10.5, 20.5):
        g = max(0.0, 1 - math.hypot(x - ex, y - 16.0) / 4.0)
        bg = tuple(min(255, round(bg[i] + (255, 40, 30)[i] * 0.25 * g)) for i in range(3))
    return bg

def write_png(path, scale):
    size = 32 * scale
    raw = bytearray()
    for py in range(size):
        raw.append(0)
        for px in range(size):
            raw.extend(pixel(px // scale, py // scale))
    def chunk(tag, data):
        return struct.pack('>I', len(data)) + tag + data + struct.pack('>I', zlib.crc32(tag + data) & 0xffffffff)
    png = b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', struct.pack('>IIBBBBB', size, size, 8, 2, 0, 0, 0)) \
        + chunk(b'IDAT', zlib.compress(bytes(raw), 9)) + chunk(b'IEND', b'')
    open(path, 'wb').write(png)

out = sys.argv[1] if len(sys.argv) > 1 else '.'
write_png(out + '/logo_512.png', 16)
write_png(out + '/icon_128.png', 4)
print('ok')
