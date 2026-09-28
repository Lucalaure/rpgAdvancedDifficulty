package crystal.champions.client.render;

import crystal.champions.config.ChampionsConfigClient;

public class ChampionsColor {

    private ChampionsColor() {
        /* This utility class should not be instantiated */
    }
    /**
     * Это использую для того, чтобы потом через кфг с hex цвета менять в числовое
     * Чуть вайб кодинга было тут
     */
    public static int applyColor(int hex) {
        // 26.x: no more setShaderColor, the tint is passed as ARGB into blit()/text()
        return 0xFF000000 | (hex & 0xFFFFFF);
    }

    public static int resetColor() {
        return 0xFFFFFFFF;
    }

    public static int parseHex(String hex) {
        try {
            return Integer.decode(hex);
        } catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    public static int getColor(int tier) {
        ChampionsConfigClient config = ChampionsConfigClient.get();

        try {
            return switch (tier) {
                case 1 -> parseHex(config.hexTier1);
                case 2 -> parseHex(config.hexTier2);
                case 3 -> parseHex(config.hexTier3);
                case 4 -> parseHex(config.hexTier4);
                default -> parseHex(config.hexTier5);
            };
        } catch (Exception e) {
            return 0xffffff;
        }
    }

}
