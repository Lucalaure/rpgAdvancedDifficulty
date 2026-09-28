package crystal.champions.client.bestiary;

import crystal.champions.affix.Affix;
import crystal.champions.affix.AffixRegistry;
import crystal.champions.bestiary.Bestiary;
import crystal.champions.util.ChampionRank;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The bestiary: a vanilla-style book with an intro page, the champion tiers,
 * then one page per affix. Affixes the player hasn't encountered yet show as "???".
 */
@Environment(EnvType.CLIENT)
public class BestiaryScreen extends BookViewScreen {
    private final @Nullable Screen parent;

    public BestiaryScreen(@Nullable Screen parent, Player player) {
        super(new BookAccess(buildPages(player)));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }

    private static List<Component> buildPages(Player player) {
        List<String> known = Bestiary.getDiscovered(player);
        Collection<Affix> affixes = AffixRegistry.ALL_AFFIXES.values();
        long found = affixes.stream().filter(affix -> known.contains(affix.getName())).count();

        List<Component> pages = new ArrayList<>();

        pages.add(Component.empty()
                .append(Component.translatable("champions.bestiary.title").withStyle(ChatFormatting.BOLD))
                .append("\n\n")
                .append(Component.translatable("champions.bestiary.intro"))
                .append("\n\n")
                .append(Component.translatable("champions.bestiary.progress", found, affixes.size()).withStyle(ChatFormatting.DARK_GREEN)));

        // A book page fits 14 lines: title, blank line, then two lines per tier
        MutableComponent tiers = Component.empty()
                .append(Component.translatable("champions.bestiary.tiers").withStyle(ChatFormatting.BOLD))
                .append("\n");
        for (ChampionRank rank : ChampionRank.RANKS) {
            if (rank.tier() <= 0) continue;
            tiers.append("\n")
                    .append(Component.literal("★".repeat(rank.tier()) + " ").withStyle(ChatFormatting.GOLD))
                    .append(Component.translatable(rank.affixes() == 1 ? "champions.bestiary.tier_affix" : "champions.bestiary.tier_affixes", rank.affixes()))
                    .append("\n")
                    .append(Component.translatable("champions.bestiary.tier_stats", format(rank.growth_h()), format(rank.growth_s()))
                            .withStyle(ChatFormatting.DARK_GRAY));
        }
        pages.add(tiers);

        for (Affix affix : affixes) {
            String name = affix.getName();
            if (known.contains(name)) {
                pages.add(Component.empty()
                        .append(Component.translatable("affix." + name).withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD))
                        .append("\n")
                        .append(Component.translatable(affix.getMobsKey()).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC))
                        .append("\n\n")
                        .append(Component.translatable("affix." + name + ".desc")));
            } else {
                pages.add(Component.empty()
                        .append(Component.literal("???").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD))
                        .append("\n\n")
                        .append(Component.translatable("champions.bestiary.unknown").withStyle(ChatFormatting.GRAY)));
            }
        }
        return pages;
    }

    private static String format(float value) {
        return value == (int) value ? String.valueOf((int) value) : String.valueOf(value);
    }
}
