from __future__ import annotations

import json
import math
import re
import sys
import urllib.request
from pathlib import Path


SOURCE_URL = "https://raw.githubusercontent.com/nvkelso/natural-earth-vector/master/geojson/ne_50m_admin_0_countries.geojson"
SCALE = 5120 / 15


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
        left = rdp(points[: index + 1], tolerance)
        right = rdp(points[index:], tolerance)
        return left[:-1] + right
    return [points[0], points[-1]]


def signed_area(points):
    total = 0.0
    for (x1, y1), (x2, y2) in zip(points, points[1:] + points[:1]):
        total += x1 * y2 - x2 * y1
    return total / 2


def marker_id(value):
    value = re.sub(r"[^a-z0-9]+", "-", value.lower()).strip("-")
    return value or "country"


def minecraft_point(lon, lat):
    return round(lon * SCALE), round(-lat * SCALE)


def ring_to_shape(ring, tolerance):
    if len(ring) > 1 and ring[0] == ring[-1]:
        ring = ring[:-1]
    simplified = rdp([(float(lon), float(lat)) for lon, lat in ring], tolerance)
    shape = []
    last = None
    for lon, lat in simplified:
        point = minecraft_point(lon, lat)
        if point != last:
            shape.append(point)
            last = point
    return shape


def iter_country_rings(feature):
    geometry = feature["geometry"]
    if not geometry:
        return []
    coordinates = geometry["coordinates"]
    if geometry["type"] == "Polygon":
        return [coordinates[0]]
    if geometry["type"] == "MultiPolygon":
        return [polygon[0] for polygon in coordinates]
    return []


def build_country_marker_block(features):
    lines = [
        "  earthliving-country-borders: {",
        '    label: "Country Borders"',
        "    toggleable: true",
        "    default-hidden: false",
        "    sorting: 20",
        "",
        "    markers: {",
    ]

    marker_count = 0
    for feature in features:
        props = feature["properties"]
        name = props.get("ADMIN") or props.get("NAME") or "Country"
        if name.lower() == "antarctica":
            continue
        iso = props.get("ISO_A3") or marker_id(name)
        rings = iter_country_rings(feature)
        ranked = sorted(rings, key=lambda ring: abs(signed_area([(float(x), float(y)) for x, y in ring])), reverse=True)

        kept = 0
        for ring in ranked:
            if kept >= 8:
                break
            shape = ring_to_shape(ring, tolerance=0.08)
            if len(shape) < 3:
                continue

            xs = [x for x, _ in shape]
            zs = [z for _, z in shape]

            # BlueMap draws straight lines across the whole map when a Natural Earth
            # polygon wraps around the +/-180 longitude seam. Keep the overlay clean
            # and skip those fragments for now; important countries still have their
            # non-wrapping mainland/island rings.
            if max(xs) - min(xs) > 90000:
                continue

            # Skip tiny fragments in the global overlay. Important islands are kept by the top-8 rule.
            if abs(signed_area(shape)) < 120:
                continue

            kept += 1
            marker_count += 1
            mid = f"{marker_id(iso)}-{kept}"
            cx = round(sum(xs) / len(xs))
            cz = round(sum(zs) / len(zs))

            lines.extend(
                [
                    f"      {mid}: {{",
                    '        type: "shape"',
                    f'        label: "{name}"',
                    f"        position: {{ x: {cx}, y: 72, z: {cz} }}",
                    "        shape-y: 72",
                    "        shape: [",
                ]
            )
            for x, z in shape:
                lines.append(f"          {{ x: {x}, z: {z} }}")
            lines.extend(
                [
                    "        ]",
                    f'        detail: "Country border overlay: {name}"',
                    "        depth-test: false",
                    "        line-width: 3",
                    "        line-color: { r: 255, g: 72, b: 72, a: 0.95 }",
                    "        fill-color: { r: 214, g: 194, b: 143, a: 0.10 }",
                    "      }",
                ]
            )

    lines.extend(["    }", "  }"])
    return "\n".join(lines), marker_count


def build_highlight_marker_block(features):
    highlighted = {
        "denmark",
        "norway",
        "sweden",
        "finland",
        "germany",
        "netherlands",
        "belgium",
        "france",
        "united kingdom",
        "ireland",
        "poland",
        "czechia",
        "austria",
        "switzerland",
        "italy",
        "spain",
        "portugal",
    }
    lines = [
        "  earthliving-highlighted-borders: {",
        '    label: "Highlighted Borders"',
        "    toggleable: true",
        "    default-hidden: false",
        "    sorting: 15",
        "",
        "    markers: {",
    ]

    marker_count = 0
    for feature in features:
        props = feature["properties"]
        name = props.get("ADMIN") or props.get("NAME") or "Country"
        if name.lower() not in highlighted:
            continue

        rings = iter_country_rings(feature)
        ranked = sorted(rings, key=lambda ring: abs(signed_area([(float(x), float(y)) for x, y in ring])), reverse=True)
        kept = 0
        for ring in ranked:
            if kept >= 12:
                break
            shape = ring_to_shape(ring, tolerance=0.035)
            if len(shape) < 3:
                continue
            xs = [x for x, _ in shape]
            zs = [z for _, z in shape]
            if max(xs) - min(xs) > 90000 or abs(signed_area(shape)) < 70:
                continue
            kept += 1
            marker_count += 1
            cx = round(sum(xs) / len(xs))
            cz = round(sum(zs) / len(zs))
            lines.extend(
                [
                    f"      {marker_id(name)}-highlight-{kept}: {{",
                    '        type: "shape"',
                    f'        label: "{name}"',
                    f"        position: {{ x: {cx}, y: 76, z: {cz} }}",
                    "        shape-y: 76",
                    "        shape: [",
                ]
            )
            for x, z in shape:
                lines.append(f"          {{ x: {x}, z: {z} }}")
            lines.extend(
                [
                    "        ]",
                    f'        detail: "Highlighted country border: {name}"',
                    "        depth-test: false",
                    "        line-width: 8",
                    "        line-color: { r: 48, g: 234, b: 255, a: 1.0 }",
                    "        fill-color: { r: 48, g: 234, b: 255, a: 0.08 }",
                    "      }",
                ]
            )

    lines.extend(["    }", "  }"])
    return "\n".join(lines), marker_count


def replace_marker_sets(config_text, marker_block):
    start = config_text.index("marker-sets: {")
    depth = 0
    end = None
    for index in range(start, len(config_text)):
        char = config_text[index]
        if char == "{":
            depth += 1
        elif char == "}":
            depth -= 1
            if depth == 0:
                end = index
                break
    if end is None:
        raise RuntimeError("Could not find marker-sets block end")

    replacement = (
        "marker-sets: {\n\n"
        "  # Generated from Natural Earth country boundary data for Earth Living.\n"
        "  # This is a visual BlueMap overlay, not production WorldGuard protection.\n\n"
        f"{marker_block}\n\n"
        "}"
    )
    return config_text[:start] + replacement + config_text[end + 1 :]


def main():
    if len(sys.argv) != 3:
        raise SystemExit("Usage: generate_bluemap_country_borders.py <input-world.conf> <output-world.conf>")

    source_path = Path("data/ne_50m_admin_0_countries.geojson")
    source_path.parent.mkdir(parents=True, exist_ok=True)
    if not source_path.exists():
        with urllib.request.urlopen(SOURCE_URL, timeout=60) as response:
            source_path.write_bytes(response.read())

    data = json.loads(source_path.read_text(encoding="utf-8"))
    country_block, country_count = build_country_marker_block(data["features"])
    highlight_block, highlight_count = build_highlight_marker_block(data["features"])
    marker_block = f"{highlight_block}\n\n{country_block}"
    input_text = Path(sys.argv[1]).read_text(encoding="utf-8")
    output_text = replace_marker_sets(input_text, marker_block)
    Path(sys.argv[2]).write_text(output_text, encoding="utf-8", newline="\n")
    print(f"Generated {country_count} country-border markers and {highlight_count} highlight markers.")


if __name__ == "__main__":
    main()
