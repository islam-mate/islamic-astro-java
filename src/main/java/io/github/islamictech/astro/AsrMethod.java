package io.github.islamictech.astro;

/**
 * Juristic school for Asr shadow factor calculation.
 */
public enum AsrMethod {
    STANDARD(1.0),
    HANAFI(2.0);

    private final double shadowFactor;

    AsrMethod(double shadowFactor) {
        this.shadowFactor = shadowFactor;
    }

    public double getShadowFactor() {
        return shadowFactor;
    }
}
