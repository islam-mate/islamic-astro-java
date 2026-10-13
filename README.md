# islamic-astro-java ☕🕌

**High-precision, zero-dependency Java library for Islamic prayer times, solar positions, and Qibla calculations.**

Works with **Java 17+, Java 21, Spring Boot, Android, Quarkus, and Micronaut**.

---

## ✨ Features
- ⚡ **Zero External Dependencies**: Standard `java.time` and `java.lang.Math` only.
- 📐 **7 Calculation Conventions**: MWL, ISNA, Egypt, Umm al-Qura, Karachi, Tehran, Jafari.
- 🧭 **High-Precision Qibla**: Forward azimuth bearing and Haversine distance to the Kaaba.
- ☕ **Modern Java**: Clean Records, Immutability, and Thread-Safe.

---

## 📦 Maven Dependency
```xml
<dependency>
    <groupId>io.github.islamictech</groupId>
    <artifactId>islamic-astro</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 🚀 Quick Start

### 1. Calculate Daily Prayer Times
```java
import io.github.islamictech.astro.*;
import java.time.*;

PrayerTimes times = PrayerTimesCalculator.calculate(
    30.0444, // Latitude (Cairo)
    31.2357, // Longitude
    LocalDate.now(),
    ZoneId.of("Africa/Cairo"),
    CalculationMethod.EGYPT,
    AsrMethod.STANDARD,
    false // isRamadan
);

System.out.println("Fajr: " + times.fajr());
System.out.println("Dhuhr: " + times.dhuhr());
System.out.println("Asr: " + times.asr());
System.out.println("Maghrib: " + times.maghrib());
System.out.println("Isha: " + times.isha());

// Or as a formatted Map<String, String>:
System.out.println(times.toTimeMap());
```

### 2. Calculate Qibla Bearing & Distance
```java
QiblaResult qibla = QiblaCalculator.calculate(30.0444, 31.2357);

System.out.printf("Bearing: %.1f° (%s)%n", qibla.bearing(), qibla.direction()); // 136.1° (SE)
System.out.printf("Distance: %.0f km%n", qibla.distanceKm());                   // 1287 km
```

---

## 📄 License
MIT License.
