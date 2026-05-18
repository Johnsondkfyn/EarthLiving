package dk.earthliving.passportborders;

import java.util.List;

final class Country {
    final String id;
    final String name;
    final String permission;
    final double price;
    final List<List<GeoPoint>> polygons;

    Country(String id, String name, String permission, double price, List<List<GeoPoint>> polygons) {
        this.id = id;
        this.name = name;
        this.permission = permission;
        this.price = price;
        this.polygons = polygons;
    }

    boolean contains(double latitude, double longitude) {
        for (List<GeoPoint> polygon : polygons) {
            if (pointInPolygon(latitude, longitude, polygon)) {
                return true;
            }
        }
        return false;
    }

    private boolean pointInPolygon(double latitude, double longitude, List<GeoPoint> polygon) {
        boolean inside = false;
        for (int i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            GeoPoint pi = polygon.get(i);
            GeoPoint pj = polygon.get(j);
            boolean intersects = ((pi.latitude > latitude) != (pj.latitude > latitude))
                    && (longitude < (pj.longitude - pi.longitude) * (latitude - pi.latitude)
                    / (pj.latitude - pi.latitude) + pi.longitude);
            if (intersects) {
                inside = !inside;
            }
        }
        return inside;
    }
}
