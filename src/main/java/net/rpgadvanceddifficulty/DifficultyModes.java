package net.rpgadvanceddifficulty;

import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.config.RpgDifficultyConfig;

/**
 * Multipliers from the game difficulty (Easy / Normal / Hard) for the difficulty scaling and champion odds.
 * Peaceful uses the Easy values (only non-hostile mobs are scaled there anyway).
 */
public final class DifficultyModes {
    private DifficultyModes() {
    }

    /** How fast mobs get stronger over time/distance. */
    public static double growth(Level level) {
        RpgDifficultyConfig c = RpgDifficultyMain.CONFIG;
        return pick(level, c.easyGrowthMultiplier, c.normalGrowthMultiplier, c.hardGrowthMultiplier);
    }

    /** Multiplier for the maximum strength (the max factor caps). */
    public static double cap(Level level) {
        RpgDifficultyConfig c = RpgDifficultyMain.CONFIG;
        return pick(level, c.easyCapMultiplier, c.normalCapMultiplier, c.hardCapMultiplier);
    }

    /** Multiplier for the chance of every champion tier. */
    public static double championChance(Level level) {
        RpgDifficultyConfig c = RpgDifficultyMain.CONFIG;
        return pick(level, c.easyChampionChance, c.normalChampionChance, c.hardChampionChance);
    }

    /** Extra multiplier per tier above 1, so higher tiers scale more with difficulty. */
    public static double championTierBonus(Level level) {
        RpgDifficultyConfig c = RpgDifficultyMain.CONFIG;
        return pick(level, c.easyChampionTierBonus, c.normalChampionTierBonus, c.hardChampionTierBonus);
    }

    /** Weight multiplier for a champion tier on this difficulty: chance * tierBonus^(tier - 1). */
    public static double tierMultiplier(Level level, int tier) {
        return tier <= 0 ? 1.0 : championChance(level) * Math.pow(championTierBonus(level), tier - 1);
    }

    private static double pick(Level level, double easy, double normal, double hard) {
        Difficulty difficulty = level.getDifficulty();
        return switch (difficulty) {
            case HARD -> hard;
            case NORMAL -> normal;
            default -> easy;
        };
    }
}
