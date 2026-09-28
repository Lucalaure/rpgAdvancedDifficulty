package net.rpgadvanceddifficulty;

import crystal.champions.IChampions;
import crystal.champions.config.ChampionsConfigServer;
import crystal.champions.util.ChampionRank;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.access.ZombieEntityAccess;
import net.rpgdifficulty.config.RpgDifficultyConfig;

import static crystal.champions.util.PrepareChampions.prepareAffixes;
import static crystal.champions.util.PrepareChampions.prepareAttributes;

/**
 * Bridges the two systems: once RpgDifficulty has scaled a freshly spawned mob,
 * this rolls its zombie variant and whether it becomes a Champion. Both rolls use
 * the same difficulty factor, so they get more common the harder the world gets.
 */
public final class ChampionSpawner {
    private ChampionSpawner() {
    }

    public static void tryMakeChampion(Mob mob, double difficultyFactor) {
        RpgDifficultyConfig config = RpgDifficultyMain.CONFIG;
        if (!config.enableChampions) return;

        IChampions champion = (IChampions) mob;
        if (champion.champions$getChampionTier() > 0) return;

        // Champions' own notion of a boss (max_boss_tier in champions_common), not the c:bosses tag
        final boolean isBoss = mob instanceof WitherBoss || mob instanceof EnderDragon;
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
     * Big / speed zombie variants. Applied on top of the difficulty-scaled stats and
     * before the champion roll, so a variant can still become a champion.
     */
    public static void tryApplyVariant(Mob mob, double difficultyFactor) {
        ChampionsConfigServer config = ChampionsConfigServer.get();
        if (!config.zombieVariants || mob.isBaby() || !(mob instanceof Zombie)) return;

        ZombieEntityAccess zombie = (ZombieEntityAccess) mob;
        if (zombie.rpgdifficulty$isBig()) return;

        double chanceMultiplier = RpgDifficultyMain.CONFIG.championsScaleWithDifficulty
                ? Math.min(RpgDifficultyMain.CONFIG.maxChampionChanceMultiplier, 1.0 + Math.max(0.0, difficultyFactor - 1.0) * RpgDifficultyMain.CONFIG.championChanceScaling)
                : 1.0;
        RandomSource random = mob.getRandom();

        if (random.nextFloat() < config.speedZombieChance / 100.0 * chanceMultiplier) {
            addBase(mob, Attributes.MAX_HEALTH, -config.speedZombieHealthMalus);
            multiplyBase(mob, Attributes.MOVEMENT_SPEED, config.speedZombieSpeed);
        } else if (random.nextFloat() < config.bigZombieChance / 100.0 * chanceMultiplier) {
            addBase(mob, Attributes.MAX_HEALTH, config.bigZombieBonusHealth);
            addBase(mob, Attributes.ATTACK_DAMAGE, config.bigZombieBonusDamage);
            multiplyBase(mob, Attributes.MOVEMENT_SPEED, config.bigZombieSlowness);
            zombie.setBig();
        } else {
            return;
        }
        mob.setHealth(mob.getMaxHealth());
    }

    private static void addBase(Mob mob, Holder<Attribute> attribute, double amount) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(Math.max(1.0, instance.getBaseValue() + amount));
        }
    }

    private static void multiplyBase(Mob mob, Holder<Attribute> attribute, double factor) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() * factor);
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
