package crystal.champions.client.render;

import crystal.champions.config.ChampionsConfigClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import static crystal.champions.client.render.ChampionsColor.applyColor;

public class ChampionsRender {

    private ChampionsRender() {
        /* This utility class should not be instantiated */
    }

    private static final Identifier CHAMPION_STARS = Identifier.fromNamespaceAndPath("champions", "textures/gui/staricon.png");
    private static final Identifier CUSTOM_BARS = Identifier.fromNamespaceAndPath("champions", "textures/gui/custom_bars.png");

    /**
     * Отрисовка и рендер
     * Все по порядку от верха к низу
     */

    public static void renderChampion(GuiGraphicsExtractor context, int centerX, int y, ChampionHudRender.ChampionData data, Minecraft client) {
        ChampionsConfigClient config = ChampionsConfigClient.get();
        int color = data.color();

        renderStars(context, centerX  + config.xOffsetStars, y + config.yOffsetStars, data.tier(), color);
        MutableComponent title = Component.literal(getMutableText(data)).append(data.name());
        context.centeredText(client.font, title, centerX + config.xOffsetText, y + config.yOffsetText, applyColor(color));

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

    private static void renderStars(GuiGraphicsExtractor context, int centerX, int y, int count, int color) {
        if (count <= 0) return;
        final int sSize = 10;
        final int totalW = (count * 11);
        final int startX = centerX - (totalW / 2);

        final int tint = applyColor(color);
        for (int i = 0; i < count; i++) {
            context.blit(RenderPipelines.GUI_TEXTURED, CHAMPION_STARS, startX + (i * 11), y, 0, 0, sSize, sSize, sSize, sSize, tint);
        }
    }

    private static void renderProgressBar(GuiGraphicsExtractor context, int centerX, int y, float percent, int color) {
        final int barW = 182;
        final int barH = 5;
        int x = centerX - barW / 2;

        final int tint = applyColor(color);
        context.blit(RenderPipelines.GUI_TEXTURED, CUSTOM_BARS, x, y, 0, 0, barW, barH, 182, 10, tint);

        int fillW = (int) (percent * barW);
        if (fillW > 0) {
            context.blit(RenderPipelines.GUI_TEXTURED, CUSTOM_BARS, x, y, 0, 5, fillW, barH, 182, 10, tint);
        }
    }

    private static void renderAffixes(GuiGraphicsExtractor context, Minecraft client, int centerX, int y, String raw) {
        String[] split = raw.split("•|·|\\s+|,");
        MutableComponent finalText = Component.empty();

        for (String s : split) {
            String t = s.trim();
            if (t.isEmpty()) continue;

            String key = t.startsWith("affix.") ? t : "affix." + t;

            if (!finalText.getSiblings().isEmpty()) {
                finalText.append(Component.literal(" • ").withStyle(ChatFormatting.DARK_GRAY));
            }

            finalText.append(Component.translatable(key).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        context.centeredText(client.font, finalText, centerX, y, 0xFFFFFFFF);
    }
}
