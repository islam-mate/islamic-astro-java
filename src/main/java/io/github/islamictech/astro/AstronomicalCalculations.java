package io.github.islamictech.astro;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.function.Function;

/**
 * Astronomical Kepler solver and solar position calculator in pure Java.
 */
public final class AstronomicalCalculations {

    private AstronomicalCalculations() {}

    public record EotDecl(double eotMinutes, double declinationRad) {}

    public static double keplerSolve(double m, double e) {
        if (e <= 0 || e >= 1) {
            throw new IllegalArgumentException("Eccentricity must be in range (0, 1)");
        }
        double bigE = m;
        for (int i = 0; i < 100; i++) {
            double delta = (bigE - e * Math.sin(bigE) - m) / (1.0 - e * Math.cos(bigE));
            bigE -= delta;
            if (Math.abs(delta) < 1e-9) {
                break;
            }
        }
        return bigE;
    }

    public static EotDecl eotDecl(Instant time) {
        // Epoch: 2000-01-01 12:00:00 UTC
        Instant epoch = Instant.parse("2000-01-01T12:00:00Z");
        double daysSinceEpoch = (time.toEpochMilli() - epoch.toEpochMilli()) / (86400.0 * 1000.0);
        double y100 = daysSinceEpoch / 36525.0;

        double e = 1.6709e-2 - 4.193e-5 * y100 - 1.26e-7 * Math.pow(y100, 2);
        double lamP = Math.toRadians(282.93807 + 1.7195 * y100 + 3.025e-4 * Math.pow(y100, 2));
        double epsilon = Math.toRadians(23.4393 - 0.013 * y100 - 2e-7 * Math.pow(y100, 2) + 5e-7 * Math.pow(y100, 3));

        double md = 6.24004077;
        double ty = 365.2596358;
        double d = daysSinceEpoch % ty;
        double m = (md + 2.0 * Math.PI * d / ty) % (2.0 * Math.PI);
        if (m < 0) m += 2.0 * Math.PI;

        double bigE = keplerSolve(m, e);
        double nu = Math.acos((Math.cos(bigE) - e) / (1.0 - e * Math.cos(bigE)));
        if (bigE > Math.PI) {
            nu = 2.0 * Math.PI - nu;
        }

        double lam = (nu + lamP) % (2.0 * Math.PI);
        if (lam < 0) lam += 2.0 * Math.PI;

        double alpha;
        if (Math.abs(Math.cos(lam)) < 1e-9) {
            alpha = lam;
        } else {
            alpha = Math.atan(Math.cos(epsilon) * Math.tan(lam));
            if (lam < Math.PI / 2.0) {
                // First quadrant
            } else if (lam < Math.PI * 1.5) {
                alpha += Math.PI;
            } else {
                alpha += 2.0 * Math.PI;
            }
        }

        double eotRad = m + lamP - alpha;
        if (eotRad > Math.PI) {
            eotRad -= 2.0 * Math.PI;
        } else if (eotRad < -Math.PI) {
            eotRad += 2.0 * Math.PI;
        }

        double eotMinutes = (eotRad / (2.0 * Math.PI)) * 1440.0;
        double decl = Math.asin(Math.sin(epsilon) * Math.sin(lam));

        return new EotDecl(eotMinutes, decl);
    }

    public static double calcAltitude(double shadowFactor, double declination, double latitude) {
        double phi = Math.toRadians(latitude);
        double delta = declination;
        double shadowFactorZenith = Math.abs(Math.tan(phi - delta));
        return Math.atan(1.0 / (shadowFactor + shadowFactorZenith));
    }

    public static double durationAtAltitudeMs(double altitude, double declination, double latitude) {
        double phi = Math.toRadians(latitude);
        double delta = declination;

        double numerator = Math.sin(altitude) - Math.sin(phi) * Math.sin(delta);
        double denominator = Math.cos(phi) * Math.cos(delta);

        if (Math.abs(denominator) < 1e-9) {
            throw new IllegalArgumentException("Sun does not reach altitude at this latitude");
        }

        double cosHourRad = numerator / denominator;
        if (cosHourRad < -1.0 || cosHourRad > 1.0) {
            throw new IllegalArgumentException("Sun does not reach the specified altitude on this date and latitude");
        }

        double hourRad = Math.acos(cosHourRad);
        double hours = (hourRad / (2.0 * Math.PI)) * 24.0;
        return hours * 3600.0 * 1000.0;
    }

    public static Instant secantInterpolate(Function<Instant, Double> diffFunc, Instant guess1, Instant guess2) {
        long g1 = guess1.toEpochMilli();
        long g2 = guess2.toEpochMilli();
        if (g2 < g1) {
            long tmp = g1;
            g1 = g2;
            g2 = tmp;
        }

        double d1 = diffFunc.apply(Instant.ofEpochMilli(g1));
        double d2 = diffFunc.apply(Instant.ofEpochMilli(g2));

        for (int i = 0; i < 50; i++) {
            double diff = d2 - d1;
            if (Math.abs(diff) < 1e-6) break;
            long g3 = Math.round(g1 - d1 * ((g2 - g1) / diff));
            double d3 = diffFunc.apply(Instant.ofEpochMilli(g3));

            if (Math.abs(g3 - g2) < 500) {
                return Instant.ofEpochMilli(g3);
            }
            g1 = g2;
            d1 = d2;
            g2 = g3;
            d2 = d3;
        }
        return Instant.ofEpochMilli(g2);
    }

    public static Instant timeZenith(LocalDate date, double longitude) {
        Instant utcNoon = date.atTime(12, 0, 0).toInstant(ZoneOffset.UTC);
        long approxZenithMs = Math.round(utcNoon.toEpochMilli() - (longitude / 15.0) * 3600.0 * 1000.0);

        Function<Instant, Double> calcDiff = guess -> {
            EotDecl eot = eotDecl(guess);
            double actual = utcNoon.toEpochMilli() - (longitude / 15.0) * 3600.0 * 1000.0 - (eot.eotMinutes() * 60000.0);
            return actual - guess.toEpochMilli();
        };

        Instant g1 = Instant.ofEpochMilli(approxZenithMs - 20 * 60 * 1000);
        Instant g2 = Instant.ofEpochMilli(approxZenithMs + 20 * 60 * 1000);
        return secantInterpolate(calcDiff, g1, g2);
    }

    public static Instant timeAltitude(Instant zenith, double altitude, double latitude, boolean rising) {
        Function<Instant, Double> calcDiff = guess -> {
            EotDecl eot = eotDecl(guess);
            double durMs = durationAtAltitudeMs(altitude, eot.declinationRad(), latitude);
            double actual = rising ? (zenith.toEpochMilli() - durMs) : (zenith.toEpochMilli() + durMs);
            return actual - guess.toEpochMilli();
        };

        Instant g1 = rising ? zenith.minusSeconds(12 * 3600) : zenith;
        Instant g2 = rising ? zenith : zenith.plusSeconds(12 * 3600);
        return secantInterpolate(calcDiff, g1, g2);
    }

    public static Instant timeShadowFactor(Instant zenith, double shadowFactor, double latitude) {
        Function<Instant, Double> calcDiff = guess -> {
            EotDecl eot = eotDecl(guess);
            double alt = calcAltitude(shadowFactor, eot.declinationRad(), latitude);
            double durMs = durationAtAltitudeMs(alt, eot.declinationRad(), latitude);
            double actual = zenith.toEpochMilli() + durMs;
            return actual - guess.toEpochMilli();
        };

        Instant g1 = zenith;
        Instant g2 = zenith.plusSeconds(12 * 3600);
        return secantInterpolate(calcDiff, g1, g2);
    }
}
