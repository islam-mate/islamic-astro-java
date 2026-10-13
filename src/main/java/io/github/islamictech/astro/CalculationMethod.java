package io.github.islamictech.astro;

/**
 * Recognized Islamic calculation conventions.
 */
public enum CalculationMethod {
    MWL(18.0, 17.0, null, null),
    ISNA(15.0, 15.0, null, null),
    EGYPT(19.5, 17.5, null, null),
    KARACHI(18.0, 18.0, null, null),
    MAKKAH(18.5, 18.5, null, 90),
    TEHRAN(17.7, 14.0, 4.5, null),
    JAFARI(16.0, 14.0, 4.0, null);

    private final double fajrDeg;
    private final double ishaDeg;
    private final Double maghribDeg;
    private final Integer ishaOffsetMinutes;

    CalculationMethod(double fajrDeg, double ishaDeg, Double maghribDeg, Integer ishaOffsetMinutes) {
        this.fajrDeg = fajrDeg;
        this.ishaDeg = ishaDeg;
        this.maghribDeg = maghribDeg;
        this.ishaOffsetMinutes = ishaOffsetMinutes;
    }

    public double getFajrDeg() { return fajrDeg; }
    public double getIshaDeg() { return ishaDeg; }
    public Double getMaghribDeg() { return maghribDeg; }
    public Integer getIshaOffsetMinutes() { return ishaOffsetMinutes; }
}
