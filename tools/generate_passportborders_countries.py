from __future__ import annotations

import json
import math
import sys
import urllib.request
from pathlib import Path


SOURCE_URL = "https://raw.githubusercontent.com/nvkelso/natural-earth-vector/master/geojson/ne_50m_admin_0_countries.geojson"
COUNTRIES = {
    "denmark": ("Danmark", 500),
    "norway": ("Norge", 650),
    "sweden": ("Sverige", 650),
    "finland": ("Finland", 650),
    "germany": ("Tyskland", 750),
    "netherlands": ("Holland", 700),
    "belgium": ("Belgien", 700),
    "france": ("Frankrig", 850),
    "united kingdom": ("Storbritannien", 850),
    "ireland": ("Irland", 700),
    "poland": ("Polen", 700),
    "czechia": ("Tjekkiet", 650),
    "austria": ("Ostrig", 650),
    "switzerland": ("Schweiz", 900),
    "italy": ("Italien", 850),
    "spain": ("Spanien", 850),
    "portugal": ("Portugal", 750),
}


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


def slug(value):
    return value.lower().replace(" ", "_").replace("-", "_").replace("'", "")


def ring_to_polygon(ring):
    if len(ring) > 1 and ring[0] == ring[-1]:
        ring = ring[:-1]
    simplified = rdp([(float(lon), float(lat)) for lon, lat in ring], tolerance=0.04)
    polygon = []
    last = None
    for lon, lat in simplified:
        point = (round(lat, 6), round(lon, 6))
        if point != last:
            polygon.append(point)
            last = point
    return polygon


def is_starter_europe_polygon(polygon):
    lats = [lat for lat, _ in polygon]
    lons = [lon for _, lon in polygon]
    center_lat = sum(lats) / len(lats)
    center_lon = sum(lons) / len(lons)
    return -25 <= center_lon <= 35 and 35 <= center_lat <= 72


def build_country(feature):
    country_name = feature["properties"].get("ADMIN") or ""
    display_name, price = COUNTRIES[country_name.lower()]
    rings = iter_rings(feature)
    ranked = sorted(rings, key=lambda ring: abs(signed_area([(float(x), float(y)) for x, y in ring])), reverse=True)
    polygons = []
    for ring in ranked:
        polygon = ring_to_polygon(ring)
        if len(polygon) < 3:
            continue
        if not is_starter_europe_polygon(polygon):
            continue
        if abs(signed_area([(lon, lat) for lat, lon in polygon])) < 0.012:
            continue
        polygons.append(polygon)
        if len(polygons) >= 12:
            break
    return display_name, price, polygons


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
        raise SystemExit("Usage: generate_passportborders_countries.py <countries.geojson> <countries.yml>")

    data = load_geojson(sys.argv[1])
    lines = ["countries:"]
    total = 0
    for feature in data["features"]:
        country_name = feature["properties"].get("ADMIN") or ""
        key = country_name.lower()
        if key not in COUNTRIES:
            continue
        display_name, price, polygons = build_country(feature)
        if not polygons:
            continue
        total += 1
        country_id = slug(country_name)
        lines.extend(
            [
                f"  {country_id}:",
                f'    name: "{display_name}"',
                f"    price: {price}",
                f'    permission: "passport.country.{country_id}"',
                "    polygons:",
            ]
        )
        for polygon in polygons:
            lines.append("      -")
            for lat, lon in polygon:
                lines.append(f"        - [{lat}, {lon}]")

    Path(sys.argv[2]).write_text("\n".join(lines) + "\n", encoding="utf-8", newline="\n")
    print(f"Generated PassportBorders countries for {total} countries.")


if __name__ == "__main__":
    main()
