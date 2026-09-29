package crystal.champions.client.bestiary;

import crystal.champions.affix.Affix;
import crystal.champions.affix.AffixRegistry;
import crystal.champions.bestiary.Bestiary;
import crystal.champions.util.ChampionRank;
import net.rpgadvanceddifficulty.DifficultyModes;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.config.RpgDifficultyConfig;
import net.minecraft.world.level.Level;
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
import java.util.Comparator;
import java.util.List;

/**
 * The bestiary: a vanilla-style book with an intro page, the champion tiers, the champion odds and
 * game difficulty effects for the current difficulty, then one page per affix grouped by affix tier.
 * Affixes the player hasn't encountered yet show as "???".
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
                    .append(Component.translatable(rank.slots() == 1 ? "champions.bestiary.tier_slot" : "champions.bestiary.tier_slots", rank.slots()))
                    .append("\n")
                    .append(Component.translatable("champions.bestiary.tier_stats", format(rank.growth_h()), format(rank.growth_s()))
                            .withStyle(ChatFormatting.DARK_GRAY));
        }
        pages.add(tiers);

        Level level = player.level();
        String difficulty = level.getDifficulty().getDisplayName().getString();
        pages.add(oddsPage(level, difficulty));

        pages.add(Component.empty()
                .append(Component.translatable("champions.bestiary.difficulty").withStyle(ChatFormatting.BOLD))
                .append("\n")
                .append(Component.translatable("champions.bestiary.odds_difficulty", difficulty).withStyle(ChatFormatting.DARK_GRAY))
                .append("\n\n")
                .append(Component.translatable("champions.bestiary.difficulty_growth", format(DifficultyModes.growth(level))))
                .append("\n")
                .append(Component.translatable("champions.bestiary.difficulty_cap", format(DifficultyModes.cap(level))))
                .append("\n")
                .append(Component.translatable("champions.bestiary.difficulty_champions", format(DifficultyModes.championChance(level))))
                .append("\n")
                .append(Component.translatable("champions.bestiary.difficulty_tiers", format(DifficultyModes.championTierBonus(level))))
                .append("\n\n")
                .append(Component.translatable("champions.bestiary.difficulty_note").withStyle(ChatFormatting.DARK_GRAY)));

        pages.add(Component.empty()
                .append(Component.translatable("champions.bestiary.affix_slots").withStyle(ChatFormatting.BOLD))
                .append("\n\n")
                .append(Component.translatable("champions.bestiary.affix_slots_text",
                        String.join(", ", ChampionRank.RANKS.stream().filter(rank -> rank.tier() > 0).map(rank -> String.valueOf(rank.slots())).toList()))));

        // Affix pages grouped by slot cost (registry order within the same cost)
        List<Affix> sorted = affixes.stream().sorted(Comparator.comparingInt(Affix::getSlots)).toList();
        for (Affix affix : sorted) {
            String name = affix.getName();
            if (known.contains(name)) {
                pages.add(Component.empty()
                        .append(Component.translatable("affix." + name).withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD))
                        .append("\n")
                        .append(tierLine(affix))
                        .append("\n")
                        .append(Component.translatable(affix.getMobsKey()).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC))
                        .append("\n\n")
                        .append(Component.translatable("affix." + name + ".desc")));
            } else {
                pages.add(Component.empty()
                        .append(Component.literal("???").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD))
                        .append("\n")
                        .append(tierLine(affix))
                        .append("\n\n")
                        .append(Component.translatable("champions.bestiary.unknown").withStyle(ChatFormatting.GRAY)));
            }
        }
        return pages;
    }

    /** Chance of each champion tier for a fresh hostile mob on the current difficulty (same maths as the real roll). */
    private static Component oddsPage(Level level, String difficulty) {
        RpgDifficultyConfig config = RpgDifficultyMain.CONFIG;
        double[] weights = ChampionRank.tierWeights(0.0, config.championChanceScaling, config.maxChampionChanceMultiplier,
                Integer.MAX_VALUE, tier -> DifficultyModes.tierMultiplier(level, tier));
        double total = 0;
        for (double weight : weights) total += weight;

        MutableComponent page = Component.empty()
                .append(Component.translatable("champions.bestiary.odds").withStyle(ChatFormatting.BOLD))
                .append("\n")
                .append(Component.translatable("champions.bestiary.odds_difficulty", difficulty).withStyle(ChatFormatting.DARK_GRAY))
                .append("\n");
        for (int i = 0; i < ChampionRank.RANKS.size(); i++) {
            ChampionRank rank = ChampionRank.RANKS.get(i);
            if (rank.tier() <= 0) continue;
            double percent = total > 0 ? weights[i] / total * 100.0 : 0.0;
            page.append("\n")
                    .append(Component.literal("★".repeat(rank.tier()) + " ").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(percent < 0.1 ? String.format("%.2f%%", percent) : String.format("%.1f%%", percent)));
        }
        return page.append("\n\n")
                .append(Component.translatable("champions.bestiary.odds_note").withStyle(ChatFormatting.DARK_GRAY));
    }

    private static Component tierLine(Affix affix) {
        int slots = affix.getSlots();
        return Component.empty()
                .append(Component.literal("◆".repeat(slots) + " ").withStyle(ChatFormatting.DARK_AQUA))
                .append(Component.translatable(slots == 1 ? "champions.bestiary.affix_slot" : "champions.bestiary.affix_slots_cost", slots));
    }

    private static String format(double value) {
        return value == (long) value ? String.valueOf((long) value) : String.valueOf(Math.round(value * 100.0) / 100.0);
    }
}
