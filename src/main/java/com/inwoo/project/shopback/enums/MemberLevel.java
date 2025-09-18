package com.inwoo.project.shopback.enums;

public enum MemberLevel {
    BRONZE("브론즈", 0),
    SILVER("실버", 100000),
    GOLD("골드", 500000),
    PLATINUM("플래티넘", 1000000),
    DIAMOND("다이아몬드", 2000000);

    private final String displayName;
    private final int requiredPoints;

    MemberLevel(String displayName, int requiredPoints) {
        this.displayName = displayName;
        this.requiredPoints = requiredPoints;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public static MemberLevel fromPoints(int points) {
        if (points >= DIAMOND.requiredPoints) return DIAMOND;
        if (points >= PLATINUM.requiredPoints) return PLATINUM;
        if (points >= GOLD.requiredPoints) return GOLD;
        if (points >= SILVER.requiredPoints) return SILVER;
        return BRONZE;
    }
}