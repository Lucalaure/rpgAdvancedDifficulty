package net.rpgdifficulty.config;

import java.util.ArrayList;
import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "rpgdifficulty")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class RpgDifficultyConfig implements ConfigData {

    @Comment("Minutes of world time (ticks the world has been running) per increase")
    public int increasingTime = 60;
    @Comment("0.1 = 10% stronger per increasingTime")
    public double timeFactor = 0.1D;

    @Comment("Also make mobs stronger the further they spawn from world spawn")
    public boolean enableDistanceScaling = false;
    @Comment("in Blocks")
    public int increasingDistance = 300;
    @Comment("0.1 = 10%")
    public double distanceFactor = 0.1D;

    @Comment("2.0 = double")
    public double maxFactorHealth = 3.0D;
    public double maxFactorDamage = 3.0D;
    public double maxFactorProtection = 1.5D;

    public boolean allowRandomValues = false;
    @Comment("in %")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int randomChance = 10;
    @Comment("in %")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int randomFactor = 10;

    @Comment("Based on other settings")
    public boolean extraXp = true;
    public float maxXPFactor = 2.0f;

    public double startingFactor = 1.0D;
    @Comment("in Blocks")
    public int startingDistance = 0;
    @Comment("in minutes")
    public int startingTime = 0;

    public boolean affectBosses = true;
    @Comment("Applies only for dimensions other than Overworld")
    public boolean excludeDistanceInOtherDimension = true;
    @Comment("Applies only for dimensions other than Overworld")
    public boolean excludeTimeInOtherDimension = false;
    @Comment("Based on health multiplier")
    public boolean dropMoreLoot = false;
    @Comment("0.02 = +2% chance per lvl")
    public float moreLootChance = 0.02F;
    public float maxLootChance = 0.7F;
    @Comment("Each loot table item has 0.5 = 50% chance to get dropped")
    public float chanceForEachItem = 0.5F;

    @Comment("Hud for testing purpose only")
    public boolean hudTesting = false;

    @Comment("Excluded Entity List Bsp: minecraft:villager")
    public ArrayList<String> excludedEntity = new ArrayList<String>(List.of("the_bumblezone:cosmic_crystal_entity"));

    @ConfigEntry.Category("monster_setting")
    @Comment("Each player increases boss attributes")
    public boolean dynamicBossModification = true;
    @ConfigEntry.Category("monster_setting")
    @Comment("0.2 = 20% per player")
    public double dynamicBossModificator = 0.3D;
    @ConfigEntry.Category("monster_setting")
    public double bossMaxFactor = 3.0D;
    @ConfigEntry.Category("monster_setting")
    public double bossDistanceFactor = 0.0D;
    @ConfigEntry.Category("monster_setting")
    public double bossTimeFactor = 0.1D;
    @ConfigEntry.Category("monster_setting")
    @Comment("Get all players in this radius")
    public double bossDistance = 256.0D;
    @ConfigEntry.Category("monster_setting")
    @Comment("Additional factor to prevent extrem explosions")
    public double creeperExplosionFactor = 1.0D;
    @ConfigEntry.Category("monster_setting")
    public boolean affectAnimalBabies = false;
    @ConfigEntry.Category("monster_setting")
    @Comment("Caps creeper explosion power after difficulty and champion tier scaling (vanilla creeper = 3, charged = 6)")
    public float maxCreeperExplosionPower = 12.0F;

    @ConfigEntry.Category("champions")
    @Comment("Allow mobs to spawn as Champions (tiers, affixes and loot are set in config/Champions)")
    public boolean enableChampions = true;
    @ConfigEntry.Category("champions")
    @Comment("Champion and zombie variant chances grow with the difficulty factor")
    public boolean championsScaleWithDifficulty = true;
    @ConfigEntry.Category("champions")
    @Comment("Tier weight multiplier = 1 + (difficultyFactor - 1) * this * tier")
    public double championChanceScaling = 1.0D;
    @ConfigEntry.Category("champions")
    @Comment("Upper limit for the tier weight multiplier")
    public double maxChampionChanceMultiplier = 8.0D;
}
