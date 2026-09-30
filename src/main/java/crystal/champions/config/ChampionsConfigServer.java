package crystal.champions.config;

import crystal.champions.Champions;
import crystal.champions.util.ChampionRank;
import crystal.champions.util.SimpleConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.Map;

public class ChampionsConfigServer {
    private static final int VERSION = 9;
    private static ChampionsConfigServer instance;

    public final int a1;
    public final int a2;
    public final int a3;
    public final int a4;
    public final int a5;
    public final int w0;
    public final int w1;
    public final int w2;
    public final int w3;
    public final int w4;
    public final int w5;
    public final float gh1;
    public final float gh2;
    public final float gh3;
    public final float gh4;
    public final float gh5;
    public final float gs1;
    public final float gs2;
    public final float gs3;
    public final float gs4;
    public final float gs5;

    public final int maxBossTier;


    private ChampionsConfigServer() {
        SimpleConfig config = SimpleConfig.of("Champions", "champions_common")
                .provider(this::defaultConfig)
                .version(VERSION)
                .request();

        this.a1 = config.getOrDefault("tier1_affix_slots", 1);
        this.a2 = config.getOrDefault("tier2_affix_slots", 2);
        this.a3 = config.getOrDefault("tier3_affix_slots", 3);
        this.a4 = config.getOrDefault("tier4_affix_slots", 4);
        this.a5 = config.getOrDefault("tier5_affix_slots", 8);

        this.w0 = config.getOrDefault("tier0_weight", 9450);
        this.w1 = config.getOrDefault("tier1_weight", 280);
        this.w2 = config.getOrDefault("tier2_weight", 150);
        this.w3 = config.getOrDefault("tier3_weight", 80);
        this.w4 = config.getOrDefault("tier4_weight", 30);
        this.w5 = config.getOrDefault("tier5_weight", 10);

        this.gh1 = ((float) config.getOrDefault("tier1_growth_health", 1.5));
        this.gh2 = ((float) config.getOrDefault("tier2_growth_health", 2.5));
        this.gh3 = ((float) config.getOrDefault("tier3_growth_health", 4.0));
        this.gh4 = ((float) config.getOrDefault("tier4_growth_health", 7.0));
        this.gh5 = ((float) config.getOrDefault("tier5_growth_health", 12.0));

        this.gs1 = ((float) config.getOrDefault("tier1_growth_strength", 1.5));
        this.gs2 = ((float) config.getOrDefault("tier2_growth_strength", 1.8));
        this.gs3 = ((float) config.getOrDefault("tier3_growth_strength", 2.2));
        this.gs4 = ((float) config.getOrDefault("tier4_growth_strength", 3.5));
        this.gs5 = ((float) config.getOrDefault("tier5_growth_strength", 5.0));

        this.maxBossTier = config.getOrDefault("max_boss_tier", 0);

    }

    private String defaultConfig(String filename) {
        return """
                # Champions Server
                # Each tier has affix slots; each affix takes 1-4 slots (see champions_affixes)
                
                # Tier 0 (not champion)
                tier0_weight = 9450
                
                # Tier 1
                tier1_affix_slots = 1
                tier1_weight = 280
                tier1_growth_health = 1.5
                tier1_growth_strength = 1.5

                # Tier 2
                tier2_affix_slots = 2
                tier2_weight = 150
                tier2_growth_health = 2.5
                tier2_growth_strength = 1.8

                # Tier 3
                tier3_affix_slots = 3
                tier3_weight = 80
                tier3_growth_health = 4.0
                tier3_growth_strength = 2.2

                # Tier 4
                tier4_affix_slots = 4
                tier4_weight = 30
                tier4_growth_health = 7.0
                tier4_growth_strength = 3.5

                # Tier 5
                tier5_affix_slots = 8
                tier5_weight = 10
                tier5_growth_health = 12.0
                tier5_growth_strength = 5.0
                
                # Maximum tier for bosses (Wither, Ender Dragon)
                max_boss_tier = 0

                """;
    }

    public static void save(Map<String, Object> changes) {
        Path path = FabricLoader.getInstance().getConfigDir()
                .resolve("Champions").resolve("champions_common.properties");
        SimpleConfig.writer(path, changes);
    }

    public static void reload() {
        instance = new ChampionsConfigServer();
        ChampionRank.get();
        Champions.LOGGER.info("champions_common reloaded!");
    }

    public static ChampionsConfigServer get() {
        if (instance == null) {
            instance = new ChampionsConfigServer();
        }
        return instance;
    }
}
