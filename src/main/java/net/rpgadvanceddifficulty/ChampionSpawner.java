package net.rpgadvanceddifficulty;

import crystal.champions.IChampions;
import crystal.champions.config.ChampionsConfigServer;
import crystal.champions.util.ChampionRank;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.config.RpgDifficultyConfig;

import static crystal.champions.util.PrepareChampions.prepareAffixes;
import static crystal.champions.util.PrepareChampions.prepareAttributes;

/**
 * Bridges the two systems: once RpgDifficulty has scaled a freshly spawned mob,
 * this rolls whether it becomes a Champion. The roll uses the same difficulty
 * factor, so champions get more common (and higher tier) with distance/time/height.
 */
public final class ChampionSpawner {
    private ChampionSpawner() {
    }

    public static void tryMakeChampion(MobEntity mob, double difficultyFactor) {
        RpgDifficultyConfig config = RpgDifficultyMain.CONFIG;
        if (!config.enableChampions) return;

        IChampions champion = (IChampions) mob;
        if (champion.champions$getChampionTier() > 0) return;

        // Champions' own notion of a boss (max_boss_tier in champions_common), not the c:bosses tag
        final boolean isBoss = mob instanceof WitherEntity || mob instanceof EnderDragonEntity;
        int maxTier = isBoss ? ChampionsConfigServer.get().maxBossTier : Integer.MAX_VALUE;
        if (maxTier <= 0 || (!isBoss && !canBeChampion(mob))) return;

        double progress = config.championsScaleWithDifficulty ? Math.max(0.0, difficultyFactor - 1.0) : 0.0;
        ChampionRank rank = ChampionRank.getRandomRank(mob.getRandom(), progress, config.championChanceScaling, config.maxChampionChanceMultiplier, maxTier);
        if (rank.tier() > 0) {
            champion.champions$setChampionTier(rank.tier());
            prepareAttributes(mob, rank);
            champion.champions$setAffixesString(prepareAffixes(rank));
        }
    }

    /**
     * Champion strength growth for the given mob, 1.0 if it is not a champion.
     * Used for damage that does not go through the attack damage attribute (arrows etc.).
     */
    public static float getStrengthMultiplier(MobEntity mob) {
        int tier = ((IChampions) mob).champions$getChampionTier();
        if (tier <= 0 || tier >= ChampionRank.RANKS.size()) return 1.0f;
        return ChampionRank.RANKS.get(tier).growth_s();
    }

    // Same eligibility rules as the original Champions mod
    private static boolean canBeChampion(MobEntity mob) {
        final boolean isAggressive = mob instanceof HostileEntity || mob instanceof Angerable
                || mob instanceof CaveSpiderEntity || mob instanceof GhastEntity
                || mob instanceof PhantomEntity || mob instanceof ShulkerEntity
                || mob instanceof SilverfishEntity || mob instanceof SlimeEntity;

        final boolean notAggressive = mob instanceof IronGolemEntity
                || mob instanceof PolarBearEntity || mob instanceof WolfEntity;

        return isAggressive && !notAggressive;
    }
}
