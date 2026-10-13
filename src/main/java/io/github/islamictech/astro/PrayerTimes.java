package io.github.islamictech.astro;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data container for calculated Islamic prayer times.
 */
public record PrayerTimes(
    LocalDate date,
    ZonedDateTime fajr,
    ZonedDateTime sunrise,
    ZonedDateTime dhuhr,
    ZonedDateTime asr,
    ZonedDateTime maghrib,
    ZonedDateTime isha,
    double latitude,
    double longitude,
    CalculationMethod method
) {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public Map<String, String> toTimeMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("fajr", fajr.format(TIME_FMT));
        map.put("sunrise", sunrise.format(TIME_FMT));
        map.put("dhuhr", dhuhr.format(TIME_FMT));
        map.put("asr", asr.format(TIME_FMT));
        map.put("maghrib", maghrib.format(TIME_FMT));
        map.put("isha", isha.format(TIME_FMT));
        return map;
    }
}
