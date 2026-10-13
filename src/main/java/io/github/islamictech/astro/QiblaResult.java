package io.github.islamictech.astro;

public record QiblaResult(
    double latitude,
    double longitude,
    double bearing,
    String direction,
    double distanceKm
) {}
