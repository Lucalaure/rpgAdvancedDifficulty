package net.rpgadvanceddifficulty;

import crystal.champions.IChampions;
import crystal.champions.config.ChampionsConfigServer;
import crystal.champions.util.ChampionRank;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.config.RpgDifficultyConfig;

import static crystal.champions.util.PrepareChampions.prepareAffixes;
import static crystal.champions.util.PrepareChampions.prepareAttributes;

/**
 * Bridges the two systems: once RpgDifficulty has scaled a freshly spawned mob,
 * this rolls whether it becomes a Champion. The roll uses the same difficulty
 * factor, so champions get more common (and higher tier) the harder the world gets.
 */
public final class ChampionSpawner {
    private ChampionSpawner() {
    }

    public static void tryMakeChampion(Mob mob, double difficultyFactor) {
        RpgDifficultyConfig config = RpgDifficultyMain.CONFIG;
        if (!config.enableChampions) return;

        IChampions champion = (IChampions) mob;
        // Already a champion, or marked as never-champion (NEVER_CHAMPION)
        if (champion.champions$getChampionTier() != 0) return;

        // Champions' own notion of a boss (max_boss_tier in champions_common), not the c:bosses tag
        final boolean isBoss = mob instanceof WitherBoss || mob instanceof EnderDragon;
        int maxTier = isBoss ? ChampionsConfigServer.get().maxBossTier : Integer.MAX_VALUE;
        if (maxTier <= 0 || (!isBoss && !canBeChampion(mob))) return;

        double progress = config.championsScaleWithDifficulty ? Math.max(0.0, difficultyFactor - 1.0) : 0.0;
        double[] weights = ChampionRank.tierWeights(progress, config.championChanceScaling, config.maxChampionChanceMultiplier, maxTier,
                tier -> DifficultyModes.tierMultiplier(mob.level(), tier));
        ChampionRank rank = ChampionRank.getRandomRank(mob.getRandom(), weights);
        if (rank.tier() > 0) {
            champion.champions$setChampionTier(rank.tier());
            prepareAttributes(mob, rank);
            champion.champions$setAffixesString(prepareAffixes(rank, mob));
            // One-time affix effects, e.g. the zombie Big/Speedy builds
            champion.champions$getActiveAffixes().forEach(affix -> affix.onApply(mob));
            mob.setHealth(mob.getMaxHealth());
        }
    }

    /**
     * Champion strength growth for the given mob, 1.0 if it is not a champion.
     * Used for damage that does not go through the attack damage attribute (arrows etc.).
     */
    public static float getStrengthMultiplier(Mob mob) {
        int tier = ((IChampions) mob).champions$getChampionTier();
        if (tier <= 0 || tier >= ChampionRank.RANKS.size()) return 1.0f;
        return ChampionRank.RANKS.get(tier).growth_s();
    }

    // Same eligibility rules as the original Champions mod
    private static boolean canBeChampion(Mob mob) {
        final boolean isAggressive = mob instanceof Monster || mob instanceof NeutralMob
                || mob instanceof CaveSpider || mob instanceof Ghast
                || mob instanceof Phantom || mob instanceof Shulker
                || mob instanceof Silverfish || mob instanceof Slime;

        final boolean notAggressive = mob instanceof IronGolem
                || mob instanceof PolarBear || mob instanceof Wolf;

        return isAggressive && !notAggressive;
    }
}
