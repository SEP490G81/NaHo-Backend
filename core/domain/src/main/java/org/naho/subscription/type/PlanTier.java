package org.naho.subscription.type;

public enum PlanTier {
    FREE(0),
    BASIC(1),
    PREMIUM(2);

    private final int level;

    PlanTier(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isHigherOrEqualThan(PlanTier other) {
        if (other == null) return true;
        return this.level >= other.level;
    }
}
