from __future__ import annotations

import json
import math
import re
import sys
import urllib.request
from pathlib import Path


SOURCE_URL = "https://raw.githubusercontent.com/nvkelso/natural-earth-vector/master/geojson/ne_50m_admin_0_countries.geojson"
SCALE = 5120 / 15


DENMARK_NAMES = {
    0: "denmark_jutland",
    1: "denmark_zealand",
    2: "denmark_funen",
    3: "denmark_lolland_falster",
    4: "denmark_bornholm",
}


def minecraft_point(lon, lat):
    return round(lon * SCALE), round(-lat * SCALE)


def point_line_distance(point, start, end):
    px, py = point
    sx, sy = start
    ex, ey = end
    dx = ex - sx
    dy = ey - sy
    if dx == 0 and dy == 0:
        return math.hypot(px - sx, py - sy)
    t = max(0, min(1, ((px - sx) * dx + (py - sy) * dy) / (dx * dx + dy * dy)))
    return math.hypot(px - (sx + t * dx), py - (sy + t * dy))


def rdp(points, tolerance):
    if len(points) <= 2:
        return points
    max_distance = -1
    index = 0
    for i in range(1, len(points) - 1):
        distance = point_line_distance(points[i], points[0], points[-1])
        if distance > max_distance:
            max_distance = distance
            index = i
    if max_distance > tolerance:
        return rdp(points[: index + 1], tolerance)[:-1] + rdp(points[index:], tolerance)
    return [points[0], points[-1]]


def signed_area(points):
    total = 0.0
    for (x1, y1), (x2, y2) in zip(points, points[1:] + points[:1]):
        total += x1 * y2 - x2 * y1
    return total / 2


def iter_rings(feature):
    geometry = feature["geometry"]
    if geometry["type"] == "Polygon":
        return [geometry["coordinates"][0]]
    if geometry["type"] == "MultiPolygon":
        return [polygon[0] for polygon in geometry["coordinates"]]
    return []


def ring_to_shape(ring):
    if len(ring) > 1 and ring[0] == ring[-1]:
        ring = ring[:-1]
    simplified = rdp([(float(lon), float(lat)) for lon, lat in ring], tolerance=0.035)
    shape = []
    last = None
    for lon, lat in simplified:
        point = minecraft_point(lon, lat)
        if point != last:
            shape.append(point)
            last = point
    return shape


def slug(value):
    value = value.lower().replace("&", "and")
    value = re.sub(r"[^a-z0-9]+", "_", value)
    return value.strip("_") or "country"


def region_name(country_name, index):
    country = slug(country_name)
    if country == "denmark" and index in DENMARK_NAMES:
        return DENMARK_NAMES[index]
    if index == 0:
        return f"{country}_main"
    return f"{country}_part_{index + 1:02d}"


def is_global_country_ring(shape):
    xs = [x for x, _ in shape]
    zs = [z for _, z in shape]
    return (max(xs) - min(xs)) <= 90000 and (max(zs) - min(zs)) <= 50000


def build_country_regions(country_feature):
    country_name = country_feature["properties"].get("ADMIN") or "country"
    rings = iter_rings(country_feature)
    ranked = sorted(rings, key=lambda ring: abs(signed_area([(float(x), float(y)) for x, y in ring])), reverse=True)

    regions = []
    for ring in ranked:
        shape = ring_to_shape(ring)
        if len(shape) < 3:
            continue
        if abs(signed_area(shape)) < 70:
            continue
        if not is_global_country_ring(shape):
            continue
        regions.append((region_name(country_name, len(regions)), shape))
        if len(regions) >= 8:
            break
    return regions


def write_regions(features, output_path):
    lines = ["regions:"]
    total = 0
    for feature in features:
        country_name = feature["properties"].get("ADMIN") or ""
        if not country_name or country_name.lower() == "antarctica":
            continue

        for name, shape in build_country_regions(feature):
            total += 1
            lines.extend(
                [
                    f"    {name}:",
                    "        min-y: -64",
                    "        max-y: 320",
                    "        points:",
                ]
            )
            for x, z in shape:
                lines.append(f"        - {{x: {x}, z: {z}}}")
            lines.extend(
                [
                    "        members: {}",
                    "        flags: {}",
                    "        owners: {}",
                    "        type: poly2d",
                    "        priority: 10",
                ]
            )

    Path(output_path).write_text("\n".join(lines) + "\n", encoding="utf-8", newline="\n")
    print(f"Generated {total} WorldGuard global country regions.")


def load_geojson(path):
    source = Path(path)
    if source.exists():
        return json.loads(source.read_text(encoding="utf-8"))

    print(f"{source} was not found. Downloading Natural Earth countries...")
    source.parent.mkdir(parents=True, exist_ok=True)
    with urllib.request.urlopen(SOURCE_URL, timeout=30) as response:
        source.write_bytes(response.read())
    return json.loads(source.read_text(encoding="utf-8"))


def main():
    if len(sys.argv) != 3:
        raise SystemExit("Usage: generate_worldguard_country_regions.py <countries.geojson> <regions.yml>")

    data = load_geojson(sys.argv[1])
    write_regions(data["features"], sys.argv[2])


if __name__ == "__main__":
    main()
