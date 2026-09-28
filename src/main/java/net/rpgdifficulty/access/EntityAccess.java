package net.rpgdifficulty.access;

public interface EntityAccess {

    public void setMobHealthMultiplier(float multiplier);

    public float getMobHealthMultiplier();

    public void setStrengthened(boolean strengthened);

    public boolean isStrengthened();
}
