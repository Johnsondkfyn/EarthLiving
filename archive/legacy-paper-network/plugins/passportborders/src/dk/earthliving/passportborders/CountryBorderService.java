package dk.earthliving.passportborders;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

final class CountryBorderService {
    private final List<Country> countries = new ArrayList<>();

    void load(File countriesFile) {
        countries.clear();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(countriesFile);
        ConfigurationSection root = config.getConfigurationSection("countries");
        if (root == null) {
            return;
        }

        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id);
            if (section == null) {
                continue;
            }

            String name = section.getString("name", id);
            String permission = section.getString("permission", "passport.country." + id.toLowerCase());
            double price = section.getDouble("price", 500.0);
            List<List<GeoPoint>> polygons = readPolygons(section.getList("polygons"));
            if (!polygons.isEmpty()) {
                countries.add(new Country(id.toLowerCase(), name, permission, price, polygons));
            }
        }
        countries.sort(Comparator.comparing(country -> country.name));
    }

    Country findBySlotIndex(int index) {
        if (index < 0 || index >= countries.size()) {
            return null;
        }
        return countries.get(index);
    }

    Country findByIdOrName(String query) {
        String normalized = normalize(query);
        for (Country country : countries) {
            if (country.id.equals(normalized) || normalize(country.name).equals(normalized)) {
                return country;
            }
        }
        return null;
    }

    Country findCountry(double latitude, double longitude) {
        for (Country country : countries) {
            if (country.contains(latitude, longitude)) {
                return country;
            }
        }
        return null;
    }

    List<Country> countries() {
        return Collections.unmodifiableList(countries);
    }

    private List<List<GeoPoint>> readPolygons(List<?> rawPolygons) {
        List<List<GeoPoint>> polygons = new ArrayList<>();
        if (rawPolygons == null) {
            return polygons;
        }

        for (Object rawPolygon : rawPolygons) {
            if (!(rawPolygon instanceof List<?> rawPoints)) {
                continue;
            }
            List<GeoPoint> points = new ArrayList<>();
            for (Object rawPoint : rawPoints) {
                GeoPoint point = readPoint(rawPoint);
                if (point != null) {
                    points.add(point);
                }
            }
            if (points.size() >= 3) {
                polygons.add(points);
            }
        }
        return polygons;
    }

    private GeoPoint readPoint(Object rawPoint) {
        if (!(rawPoint instanceof List<?> values) || values.size() < 2) {
            return null;
        }

        Double latitude = readDouble(values.get(0));
        Double longitude = readDouble(values.get(1));
        if (latitude == null || longitude == null) {
            return null;
        }
        return new GeoPoint(latitude, longitude);
    }

    private Double readDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text) {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
    }
}
