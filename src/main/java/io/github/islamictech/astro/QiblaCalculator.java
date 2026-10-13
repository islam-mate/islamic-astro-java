package io.github.islamictech.astro;

public final class QiblaCalculator {

    public static final double KAABA_LATITUDE = 21.4225;
    public static final double KAABA_LONGITUDE = 39.8262;
    public static final double EARTH_RADIUS_KM = 6371.0;

    private static final String[] DIRECTIONS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    private QiblaCalculator() {}

    public static double calculateBearing(double lat, double lng) {
        double lat1 = Math.toRadians(lat);
        double lat2 = Math.toRadians(KAABA_LATITUDE);
        double deltaLng = Math.toRadians(KAABA_LONGITUDE - lng);

        double x = Math.sin(deltaLng) * Math.cos(lat2);
        double y = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLng);

        double bearingDeg = Math.toDegrees(Math.atan2(x, y));
        return (bearingDeg + 360.0) % 360.0;
    }

    public static double calculateDistance(double lat, double lng) {
        double lat1 = Math.toRadians(lat);
        double lat2 = Math.toRadians(KAABA_LATITUDE);
        double deltaLat = Math.toRadians(KAABA_LATITUDE - lat);
        double deltaLng = Math.toRadians(KAABA_LONGITUDE - lng);

        double a = Math.pow(Math.sin(deltaLat / 2.0), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(deltaLng / 2.0), 2);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(Math.max(0.0, 1.0 - a)));
        return EARTH_RADIUS_KM * c;
    }

    public static String bearingToDirection(double bearing) {
        int index = (int) Math.round(bearing / 45.0) % 8;
        return DIRECTIONS[index];
    }

    public static QiblaResult calculate(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }

        double bearing = calculateBearing(latitude, longitude);
        double distance = calculateDistance(latitude, longitude);
        String direction = bearingToDirection(bearing);

        return new QiblaResult(
            latitude,
            longitude,
            Math.round(bearing * 100.0) / 100.0,
            direction,
            Math.round(distance * 100.0) / 100.0
        );
    }
}
