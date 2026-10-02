package net.rpgadvanceddifficulty;

import net.minecraft.world.entity.Mob;

/**
 * Entity tags other mods, datapacks or commands can put on a mob before it spawns to change its champion odds,
 * without depending on this mod's code (e.g. Bloodmoon Events tags its horde mobs):
 *
 * <pre>
 * rpgadvanceddifficulty.champion_chance.2.5      every champion tier is 2.5x as likely
 * rpgadvanceddifficulty.champion_tier_bonus.1.2  each tier above 1 is another 1.2x (higher tiers gain more)
 * </pre>
 *
 * Both multiply on top of the normal odds (difficulty, game difficulty). Missing or invalid tags count as 1.
 * Example: /summon minecraft:zombie ~ ~ ~ {Tags:["rpgadvanceddifficulty.champion_chance.10"]}
 */
public final class ChampionTags {
    private ChampionTags() {
    }

    public static final String CHANCE_PREFIX = "rpgadvanceddifficulty.champion_chance.";
    public static final String TIER_BONUS_PREFIX = "rpgadvanceddifficulty.champion_tier_bonus.";

    /** Weight multiplier for a champion tier from the mob's tags: chance * tierBonus^(tier - 1). */
    public static double tierMultiplier(Mob mob, int tier) {
        if (tier <= 0 || mob.entityTags().isEmpty()) return 1.0;
        return read(mob, CHANCE_PREFIX) * Math.pow(read(mob, TIER_BONUS_PREFIX), tier - 1);
    }

    private static double read(Mob mob, String prefix) {
        double value = 1.0;
        for (String tag : mob.entityTags()) {
            if (tag.startsWith(prefix)) {
                try {
                    value *= Math.max(0.0, Double.parseDouble(tag.substring(prefix.length())));
                } catch (NumberFormatException ignored) {
                    // Not a number: ignore the tag
                }
            }
        }
        return value;
    }
}
