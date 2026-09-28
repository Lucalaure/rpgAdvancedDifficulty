package crystal.champions.client.render;

import crystal.champions.config.ChampionsConfigClient;
import net.minecraft.client.gui.DrawContext;

public class ChampionsColor {

    private ChampionsColor() {
        /* This utility class should not be instantiated */
    }
    /**
     * Это использую для того, чтобы потом через кфг с hex цвета менять в числовое
     * Чуть вайб кодинга было тут
     */
    public static void applyColor(DrawContext context, int hex) {
        final float r = (hex >> 16 & 255) / 255.0F;
        final float g = (hex >> 8 & 255) / 255.0F;
        final float b = (hex & 255) / 255.0F;
        context.setShaderColor(r, g, b, 1.0F);
    }

    public static void resetColor(DrawContext context) {
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
