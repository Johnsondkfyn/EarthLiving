from __future__ import annotations

import json
import math
import re
import sys
import urllib.request
from pathlib import Path


SOURCE_URL = "https://raw.githubusercontent.com/nvkelso/natural-earth-vector/master/geojson/ne_50m_admin_0_countries.geojson"
INCOME_BASE_PRICES = {
    "1. High income: OECD": 1000,
    "2. High income: nonOECD": 875,
    "3. Upper middle income": 675,
    "4. Lower middle income": 475,
    "5. Low income": 325,
}

COUNTRY_NAME_OVERRIDES = {
    "Czechia": "Czech Republic",
    "United Kingdom": "United Kingdom",
    "United States of America": "United States",
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
    text = value.lower().replace("&", "and")
    text = re.sub(r"[^a-z0-9]+", "_", text)
    return text.strip("_") or "country"


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


def price_for(feature):
    props = feature["properties"]
    income_group = props.get("INCOME_GRP", "")
    base = INCOME_BASE_PRICES.get(income_group, 550)
    population = float(props.get("POP_EST") or 0)
    gdp_million = float(props.get("GDP_MD") or 0)
    gdp_per_capita = (gdp_million * 1_000_000 / population) if population > 0 else 0

    if gdp_per_capita >= 65000:
        base += 250
    elif gdp_per_capita >= 45000:
        base += 150
    elif gdp_per_capita >= 25000:
        base += 75
    elif 0 < gdp_per_capita < 2500:
        base -= 75

    if population >= 100_000_000:
        base += 75
    elif population <= 300_000:
        base = max(250, base - 100)

    return max(250, min(1500, int(round(base / 25) * 25)))


def build_country(feature):
    country_name = feature["properties"].get("ADMIN") or ""
    display_name = COUNTRY_NAME_OVERRIDES.get(country_name, country_name)
    price = price_for(feature)
    rings = iter_rings(feature)
    ranked = sorted(rings, key=lambda ring: abs(signed_area([(float(x), float(y)) for x, y in ring])), reverse=True)
    polygons = []
    for ring in ranked:
        polygon = ring_to_polygon(ring)
        if len(polygon) < 3:
            continue
        if abs(signed_area([(lon, lat) for lat, lon in polygon])) < 0.006:
            continue
        polygons.append(polygon)
        if len(polygons) >= 18:
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
        if not country_name or country_name.lower() == "antarctica":
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
