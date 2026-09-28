package crystal.champions;

import crystal.champions.config.ChampionsConfigClient;

public class ChampionsColorServer {
    private ChampionsColorServer() {
        /* This utility class should not be instantiated */
    }

    private static int parseHex(String hex) {
        try {
            return Integer.decode(hex);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    public static int getColor(int tier) {
        ChampionsConfigClient cfg = ChampionsConfigClient.get();

        try {
            return switch (tier) {
                case 1 -> parseHex(cfg.hexTier1);
                case 2 -> parseHex(cfg.hexTier2);
                case 3 -> parseHex(cfg.hexTier3);
                case 4 -> parseHex(cfg.hexTier4);
                default -> parseHex(cfg.hexTier5);
            };
        } catch (Exception e) {
            return 0xffffff;
        }
    }
}
