package crystal.champions.config;

import crystal.champions.Champions;
import crystal.champions.affix.AffixRegistry;
import crystal.champions.util.SimpleConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.Map;

public class ChampionsConfigAffixes {
    private static final int VERSION = 2;
    private static ChampionsConfigAffixes instance;

    public final int cooldownBeforeBulletArtic;
    public final float dampeningAmount;
    public final int cooldownBeforeBulletMolten;
    public final byte hastyAmplifier;
    public final int poisonDuration;
    public final byte poisonAmplifier;
    public final int timeBeforeDesecrating;
    public final int cloudDuration;
    public final double knockback;
    public final int timeBeforeInfected;
    public final int maxSilverFishCount;
    public final short infectedSilverfish;
    public final float infectedFactorHealth;
    public final double strength;
    public final int entityHeal;
    public final int entityHealNoTarget;
    public final int entityHealTime;
    public final int reflectionDamage;
    public final int shieldAllTime;
    public final int shieldWork;
    public final float blindChance;
    public final int blindDuration;
    public final float paralyzeChance;
    public final int paralyzeDuration;
    public final int magneticCooldown;
    public final int magneticPullTime;

    public final boolean r1;
    public final boolean r2;
    public final boolean r3;
    public final boolean r4;
    public final boolean r5;
    public final boolean r6;
    public final boolean r7;
    public final boolean r8;
    public final boolean r9;
    public final boolean r10;
    public final boolean r11;
    public final boolean r12;
    public final boolean r13;
    public final boolean r14;
    public final boolean r15;

    // Использую SimpleConfig
    // https://github.com/magistermaks/fabric-simplelibs/blob/master/simple-config/SimpleConfig.java
    private ChampionsConfigAffixes() {
        SimpleConfig config = SimpleConfig.of("Champions", "champions_affixes")
                .provider(this::defaultConfig)
                .version(VERSION)
                .request();

        cooldownBeforeBulletArtic = config.getOrDefault("cooldown_arctic", 160);

        dampeningAmount = (float) config.getOrDefault("dampening_amount", 0.5);

        timeBeforeDesecrating = config.getOrDefault("time_before_desecrating", 1000);
        cloudDuration = config.getOrDefault("cloud_desecrating_duration", 200);

        hastyAmplifier = (byte) config.getOrDefault("hasty_amplifier", 4);

        timeBeforeInfected = config.getOrDefault("time_before_infecting", 600);
        maxSilverFishCount = config.getOrDefault("max_silverfish_count", 10);
        infectedSilverfish = (short) config.getOrDefault("infected_silverfish", 4);
        infectedFactorHealth = (float) config.getOrDefault("infected_factor_health", 0.0);

        knockback = (float) config.getOrDefault("knocking_strength", 1.5);

        entityHeal = config.getOrDefault("entity_heal", 1);
        entityHealNoTarget = config.getOrDefault("entity_heal_no_target", 4);
        entityHealTime = config.getOrDefault("when_entity_heal", 20);

        magneticCooldown = config.getOrDefault("magnetic_cooldown", 600);
        magneticPullTime = config.getOrDefault("magnetic_pull_time", 150);
        strength = config.getOrDefault("magnetic_strength", 1);

        cooldownBeforeBulletMolten = config.getOrDefault("cooldown_molten", 90);

        poisonDuration = config.getOrDefault("plagued_poison_duration", 200);
        poisonAmplifier = (byte) config.getOrDefault("plagued_poison_amplifier", 0);

        reflectionDamage = config.getOrDefault("reflection_damage", 2);

        shieldAllTime = config.getOrDefault("shield_all_time", 300);
        shieldWork = config.getOrDefault("shield_working_time", 60);

        blindChance = (float) config.getOrDefault("blind_chance", 0.2);
        blindDuration = config.getOrDefault("blind_duration", 80);

        paralyzeChance = (float) config.getOrDefault("paralyzing_chance", 0.1);
        paralyzeDuration = config.getOrDefault("paralyzing_duration", 60);

        r1 = config.getOrDefault("hasty_affix", true);
        r2 = config.getOrDefault("arctic_affix", true);
        r3 = config.getOrDefault("molten_affix", true);
        r4 = config.getOrDefault("desecrating_affix", true);
        r5 = config.getOrDefault("plagued_affix", true);
        r6 = config.getOrDefault("infected_affix", true);
        r7 = config.getOrDefault("adaptive_affix", true);
        r8 = config.getOrDefault("knocking_affix", true);
        r9 = config.getOrDefault("shielding_affix", true);
        r10 = config.getOrDefault("reflective_affix", true);
        r11 = config.getOrDefault("magnetic_affix", true);
        r12 = config.getOrDefault("dampening_affix", true);
        r13 = config.getOrDefault("lively_affix", true);
        r14 = config.getOrDefault("blinded_affix", true);
        r15 = config.getOrDefault("paralyzing_affix", true);
    }

    private String defaultConfig(String filename) {
        return """
                # Champions Affixes
                
                # Registry
                # If true, the affix will be in the pool
                hasty_affix = true
                arctic_affix = true
                molten_affix = true
                desecrating_affix = true
                plagued_affix = true
                infected_affix = true
                adaptive_affix = true
                knocking_affix = true
                shielding_affix = true
                reflective_affix = true
                magnetic_affix = true
                dampening_affix = true
                lively_affix = true
                blinded_affix = true
                paralyzing_affix = true
                
                # Arctic
                # Cooldown between arctic bullets (ticks)
                cooldown_arctic = 160
                # Molten
                # Cooldown between molten bullets (ticks)
                cooldown_molten = 90
                
                # Desecrating
                # Ticks between desecrating clouds
                time_before_desecrating = 1000
                # Duration of the cloud (ticks)
                cloud_desecrating_duration = 200
                
                # Plagued
                # Duration of poison effect (ticks)
                plagued_poison_duration = 200
                # Poison amplifier
                plagued_poison_amplifier = 0
                
                # Infected
                # Ticks between spawning silverfish
                time_before_infecting = 600
                # Max silverfish that can exist around champion
                max_silverfish_count = 10
                # Base number of silverfish to spawn
                infected_silverfish = 4
                # Additional silverfish (health * factor)
                infected_factor_health = 0
                
                # Reflective
                # Damage reflected back to the attacker
                reflection_damage = 2
                
                # Healing
                # Amount of heal
                entity_heal = 1
                # Amount of heal when champions doesn't have target
                entity_heal_no_target = 4
                # Ticks between healing
                when_entity_heal = 20
                
                # Knocking
                knocking_strength = 1.5
                
                # Magnetic
                # Cooldown duration (ticks)
                magnetic_cooldown = 600
                # Pull time (ticks)
                magnetic_pull_time = 150
                # Pull strength
                magnetic_strength = 1
                
                # Hasty
                # Speed effect amplifier
                hasty_amplifier = 4
                
                # Damage reduction multiplier (0.5 = 50% less damage taken)
                dampening_amount = 0.5
                
                # Shield
                # Cycle time of the shield (ticks)
                shield_all_time = 300
                # How long the shield stays active (ticks)
                shield_working_time = 60
                
                # Blinded
                # Chance to blind (1.0 - 0)
                blind_chance = 0.2
                # Blind effect duration (ticks)
                blind_duration = 80
                
                # Paralyzing
                # Chance to paralyze (1.0 - 0)
                paralyzing_chance = 0.1
                # Paralyze effect duration (ticks)
                paralyzing_duration = 60
                """;
    }

    public static void save(Map<String, Object> changes) {
        Path path = FabricLoader.getInstance().getConfigDir()
                .resolve("Champions").resolve("champions_affixes.properties");
        SimpleConfig.writer(path, changes);
    }

    public static void reload() {
        instance = new ChampionsConfigAffixes();
        AffixRegistry.affixesRegister();
        Champions.LOGGER.info("champions_affixes reloaded!");
    }

    public static ChampionsConfigAffixes get() {
        if (instance == null) {
            instance = new ChampionsConfigAffixes();
        }
        return instance;
    }
}
