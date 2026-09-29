package crystal.champions.util;

import crystal.champions.affix.Affix;
import crystal.champions.affix.AffixRegistry;
import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrepareChampions {
    private PrepareChampions() {
        /* This utility class should not be instantiated */
    }

    public static void prepareAttributes(Mob mob, ChampionRank rank) {
        final float h = rank.growth_h();
        final float s = rank.growth_s();
        modifyAttribute(mob, Attributes.MAX_HEALTH, h);
        mob.setHealth(mob.getMaxHealth());

        if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            modifyAttribute(mob, Attributes.ATTACK_DAMAGE, s);
        }
    }

    /**
     * Fills the tier's affix slots. Each affix costs its slot count, so e.g. a tier 2 champion (2 slots)
     * gets either two 1-slot affixes or one 2-slot affix. Picks are weighted towards bigger affixes.
     */
    public static String prepareAffixes(ChampionRank rank, Mob mob) {
        // Only affixes this mob can have (mob-specific ones) and that fit in this tier at all
        List<Affix> pool = new ArrayList<>(AffixRegistry.ALL_AFFIXES.values().stream()
                .filter(affix -> affix.canApplyTo(mob) && affix.getSlots() <= rank.slots()).toList());
        final float bonus = ChampionsConfigAffixes.get().affixSlotWeightBonus;

        List<String> selected = new ArrayList<>();
        Set<String> usedGroups = new HashSet<>();
        int remaining = rank.slots();
        while (remaining > 0) {
            final int free = remaining;
            List<Affix> fits = pool.stream()
                    .filter(affix -> affix.getSlots() <= free && (affix.getExclusiveGroup() == null || !usedGroups.contains(affix.getExclusiveGroup())))
                    .toList();
            if (fits.isEmpty()) break;

            // Weighted pick among the affixes that still fit
            double total = 0;
            for (Affix affix : fits) total += affixWeight(affix, bonus);
            double roll = mob.getRandom().nextDouble() * total;
            Affix picked = fits.getLast();
            for (Affix affix : fits) {
                roll -= affixWeight(affix, bonus);
                if (roll < 0) {
                    picked = affix;
                    break;
                }
            }

            pool.remove(picked);
            if (picked.getExclusiveGroup() != null) usedGroups.add(picked.getExclusiveGroup());
            selected.add(picked.getName());
            remaining -= picked.getSlots();
        }

        return String.join(",", selected);
    }

    /** Lowest champion tier with enough slots for this many (falls back to the highest tier). */
    public static ChampionRank lowestRankWithSlots(int slotsNeeded) {
        for (ChampionRank rank : ChampionRank.RANKS) {
            if (rank.tier() > 0 && rank.slots() >= slotsNeeded) return rank;
        }
        return ChampionRank.RANKS.getLast();
    }

    public static double affixWeight(Affix affix, float slotWeightBonus) {
        return 1.0 + slotWeightBonus * (affix.getSlots() - 1);
    }

    private static void modifyAttribute(Mob entity, Holder<Attribute> attribute, float m) {
        var instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() * m);
        }
    }
}
