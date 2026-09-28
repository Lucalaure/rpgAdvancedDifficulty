package crystal.champions.config;

import crystal.champions.Champions;
import crystal.champions.util.SimpleConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.Map;

public class ChampionsConfigClient {
    private static final int VERSION = 1;
    private static ChampionsConfigClient instance;

    public final int yOffsetStars;
    public final int yOffsetText;
    public final int yOffsetBar;
    public final int yOffsetAffixes;

    public final int xOffsetStars;
    public final int xOffsetText;
    public final int xOffsetBar;
    public final int xOffsetAffixes;

    public final short cacheClient;
    public final short cacheServer;
    public final boolean onlyForView;

    public final String hexTier1;
    public final String hexTier2;
    public final String hexTier3;
    public final String hexTier4;
    public final String hexTier5;

    public final boolean alwaysRenderBox;


    private ChampionsConfigClient() {
        SimpleConfig config = SimpleConfig.of("Champions","champions_client")
                .provider(this::defaultConfig)
                .version(VERSION)
                .request();

        this.yOffsetStars = config.getOrDefault("y_offset_stars", -5);
        this.yOffsetText = config.getOrDefault("y_offset_text", 7);
        this.yOffsetBar = config.getOrDefault("y_offset_bar", 19);
        this.yOffsetAffixes = config.getOrDefault("y_offset_affixes", 29);

        this.xOffsetStars = config.getOrDefault("x_offset_stars", 0);
        this.xOffsetText = config.getOrDefault("x_offset_text", 0);
        this.xOffsetBar = config.getOrDefault("x_offset_bar", 0);
        this.xOffsetAffixes = config.getOrDefault("x_offset_affixes", 0);

        this.hexTier1 = config.getOrDefault("hex_tier_1", "#FFFF55");
        this.hexTier2 = config.getOrDefault("hex_tier_2", "#F57C2C");
        this.hexTier3 = config.getOrDefault("hex_tier_3", "#46DFFA");
        this.hexTier4 = config.getOrDefault("hex_tier_4", "#8823DB");
        this.hexTier5 = config.getOrDefault("hex_tier_5", "#F98AFF");

        this.alwaysRenderBox = config.getOrDefault("always_render", false);

        this.cacheClient = (short) config.getOrDefault("cache_client", 1000);
        this.cacheServer = (short) config.getOrDefault("cache_server", 5000);
        this.onlyForView = config.getOrDefault("only_for_view", false);
    }

    private String defaultConfig(String filename) {
        return """
                # Champions Client
                
                # Champions color (Hex)
                hex_tier_1 = #FFFF55
                hex_tier_2 = #F57C2C
                hex_tier_3 = #46DFFA
                hex_tier_4 = #8823DB
                hex_tier_5 = #F98AFF
                
                # Champions HUD position
                y_offset_stars = -5
                y_offset_text = 7
                y_offset_bar = 19
                y_offset_affixes = 29
                
                x_offset_stars = 0
                x_offset_text = 0
                x_offset_bar = 0
                x_offset_affixes = 0
                
                # Will champions render through walls when cache_server has champions?
                always_render = false
                
                # Time cache for champions (millis) (max = 32767)
                cache_client = 1000
                cache_server = 5000
                # If true, it will be render only when looking at the champion
                only_for_view = false
                """;
    }

    public static void save(Map<String, Object> changes) {
        Path path = FabricLoader.getInstance().getConfigDir()
                .resolve("Champions").resolve("champions_client.properties");
        SimpleConfig.writer(path, changes);
    }

    public static void reload() {
        instance = new ChampionsConfigClient();
        Champions.LOGGER.info("champions_client reloaded!");
    }

    public static ChampionsConfigClient get() {
        if (instance == null) {
            instance = new ChampionsConfigClient();
        }
        return instance;
    }
}
