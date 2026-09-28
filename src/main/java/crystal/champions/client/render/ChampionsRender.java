package crystal.champions.client.render;

import crystal.champions.config.ChampionsConfigClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static crystal.champions.client.render.ChampionsColor.applyColor;
import static crystal.champions.client.render.ChampionsColor.resetColor;

public class ChampionsRender {

    private ChampionsRender() {
        /* This utility class should not be instantiated */
    }

    private static final Identifier CHAMPION_STARS = Identifier.of("champions", "textures/gui/staricon.png");
    private static final Identifier CUSTOM_BARS = Identifier.of("champions", "textures/gui/custom_bars.png");

    /**
     * Отрисовка и рендер
     * Все по порядку от верха к низу
     */

    public static void renderChampion(DrawContext context, int centerX, int y, ChampionHudRender.ChampionData data, MinecraftClient client) {
        ChampionsConfigClient config = ChampionsConfigClient.get();
        int color = data.color();

        renderStars(context, centerX  + config.xOffsetStars, y + config.yOffsetStars, data.tier(), color);
        MutableText title = Text.literal(getMutableText(data)).append(data.name());
        context.drawCenteredTextWithShadow(client.textRenderer, title, centerX + config.xOffsetText, y + config.yOffsetText, color);

        renderProgressBar(context, centerX + config.xOffsetBar, y + config.yOffsetBar, data.percent(), color);

        if (data.affixes() != null && !data.affixes().isEmpty()) {
            renderAffixes(context, client, centerX + config.xOffsetAffixes, y + config.yOffsetAffixes, data.affixes());
        }
    }

    private static String getMutableText(ChampionHudRender.ChampionData data) {
        return switch (data.tier()) {
            case (1) -> "Skilled ";
            case (2) -> "Elite ";
            case (3) -> "Legendary ";
            case (4) -> "Ultimate ";
            case (5) -> "Mythical ";
            default -> "Skilled ";
        };
    }

    private static void renderStars(DrawContext context, int centerX, int y, int count, int color) {
        if (count <= 0) return;
        final int sSize = 10;
        final int totalW = (count * 11);
        final int startX = centerX - (totalW / 2);

        applyColor(context, color);
        for (int i = 0; i < count; i++) {
            context.drawTexture(CHAMPION_STARS, startX + (i * 11), y, 0, 0, sSize, sSize, sSize, sSize);
        }
        resetColor(context);
    }

    private static void renderProgressBar(DrawContext context, int centerX, int y, float percent, int color) {
        final int barW = 182;
        final int barH = 5;
        int x = centerX - barW / 2;

        applyColor(context, color);
        context.drawTexture(CUSTOM_BARS, x, y, 0, 0, barW, barH, 182, 10);

        int fillW = (int) (percent * barW);
        if (fillW > 0) {
            context.drawTexture(CUSTOM_BARS, x, y, 0, 5, fillW, barH, 182, 10);
            resetColor(context);
        } else {
            resetColor(context);
        }
    }

    private static void renderAffixes(DrawContext context, MinecraftClient client, int centerX, int y, String raw) {
        String[] split = raw.split("•|·|\\s+|,");
        MutableText finalText = Text.empty();

        for (String s : split) {
            String t = s.trim();
            if (t.isEmpty()) continue;

            String key = t.startsWith("affix.") ? t : "affix." + t;

            if (!finalText.getSiblings().isEmpty()) {
                finalText.append(Text.literal(" • ").formatted(Formatting.DARK_GRAY));
            }

            finalText.append(Text.translatable(key).formatted(Formatting.GRAY, Formatting.ITALIC));
        }
        context.drawCenteredTextWithShadow(client.textRenderer, finalText, centerX, y, 0xFFFFFF);
    }
}
