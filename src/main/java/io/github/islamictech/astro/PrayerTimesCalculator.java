package io.github.islamictech.astro;

import java.time.*;

/**
 * Main calculator service for prayer times in Java.
 */
public final class PrayerTimesCalculator {

    private PrayerTimesCalculator() {}

    public static PrayerTimes calculate(
        double latitude,
        double longitude,
        LocalDate date,
        ZoneId zoneId,
        CalculationMethod method,
        AsrMethod asrMethod,
        boolean isRamadan
    ) {
        double shadowFactor = asrMethod.getShadowFactor();
        double sunsetAlt = Math.toRadians(-0.833);
        double fajrAlt = Math.toRadians(-method.getFajrDeg());
        double ishaAlt = Math.toRadians(-method.getIshaDeg());

        Instant zenith = AstronomicalCalculations.timeZenith(date, longitude);
        Instant fajrInst = AstronomicalCalculations.timeAltitude(zenith, fajrAlt, latitude, true);
        Instant sunriseInst = AstronomicalCalculations.timeAltitude(zenith, sunsetAlt, latitude, true);
        Instant asrInst = AstronomicalCalculations.timeShadowFactor(zenith, shadowFactor, latitude);

        Instant maghribInst;
        if (method.getMaghribDeg() != null) {
            maghribInst = AstronomicalCalculations.timeAltitude(zenith, Math.toRadians(-method.getMaghribDeg()), latitude, false);
        } else {
            maghribInst = AstronomicalCalculations.timeAltitude(zenith, sunsetAlt, latitude, false);
        }

        Instant ishaInst;
        if (method.getIshaOffsetMinutes() != null) {
            int offsetMin = isRamadan ? 120 : method.getIshaOffsetMinutes();
            ishaInst = maghribInst.plusSeconds(offsetMin * 60L);
        } else {
            ishaInst = AstronomicalCalculations.timeAltitude(zenith, ishaAlt, latitude, false);
        }

        return new PrayerTimes(
            date,
            fajrInst.atZone(zoneId),
            sunriseInst.atZone(zoneId),
            zenith.atZone(zoneId),
            asrInst.atZone(zoneId),
            maghribInst.atZone(zoneId),
            ishaInst.atZone(zoneId),
            latitude,
            longitude,
            method
        );
    }

    public static PrayerTimes calculate(double latitude, double longitude, ZoneId zoneId) {
        return calculate(latitude, longitude, LocalDate.now(zoneId), zoneId, CalculationMethod.EGYPT, AsrMethod.STANDARD, false);
    }
}
