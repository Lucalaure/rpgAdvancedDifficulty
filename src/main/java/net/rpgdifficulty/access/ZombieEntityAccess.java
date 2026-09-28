package net.rpgdifficulty.access;

public interface ZombieEntityAccess {

    void setBig();

    // Tracked "BIG_ZOMBIE" boolean, synced to the client for the renderer
    boolean rpgdifficulty$isBig();
}
