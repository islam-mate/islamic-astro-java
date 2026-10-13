package io.github.islamictech.astro;

import java.time.LocalDate;
import java.time.ZoneId;

public class AstroTestRunner {
    public static void main(String[] args) {
        System.out.println("Running Java Astro tests...");

        // 1. Prayer times test
        PrayerTimes pt = PrayerTimesCalculator.calculate(
            30.0444, 31.2357, LocalDate.of(2026, 1, 15), ZoneId.of("Africa/Cairo"),
            CalculationMethod.EGYPT, AsrMethod.STANDARD, false
        );

        assert pt.fajr().isBefore(pt.sunrise()) : "Fajr must be before sunrise";
        assert pt.sunrise().isBefore(pt.dhuhr()) : "Sunrise must be before dhuhr";
        assert pt.dhuhr().isBefore(pt.asr()) : "Dhuhr must be before asr";
        assert pt.asr().isBefore(pt.maghrib()) : "Asr must be before maghrib";
        assert pt.maghrib().isBefore(pt.isha()) : "Maghrib must be before isha";
        System.out.println("✔ Prayer times test passed: " + pt.toTimeMap());

        // 2. Qibla test
        QiblaResult qibla = QiblaCalculator.calculate(30.0444, 31.2357);
        assert qibla.bearing() >= 130.0 && qibla.bearing() <= 140.0 : "Cairo bearing should be ~136°";
        assert "SE".equals(qibla.direction()) : "Direction should be SE";
        assert qibla.distanceKm() >= 1200.0 && qibla.distanceKm() <= 1400.0 : "Distance should be ~1287km";
        System.out.println("✔ Qibla test passed: bearing=" + qibla.bearing() + "°, direction=" + qibla.direction() + ", distance=" + qibla.distanceKm() + "km");

        System.out.println("ALL JAVA TESTS PASSED SUCCESSFULLY!");
    }
}
