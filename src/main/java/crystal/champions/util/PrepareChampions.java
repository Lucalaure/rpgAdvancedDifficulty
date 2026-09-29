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

    public static String prepareAffixes(ChampionRank rank, Mob mob) {
        // Only affixes this mob can have (mob-specific ones) and that this tier has unlocked
        List<Affix> pool = new ArrayList<>(AffixRegistry.ALL_AFFIXES.values().stream()
                .filter(affix -> affix.canApplyTo(mob) && affix.getMinTier() <= rank.tier()).toList());
        final float bonus = ChampionsConfigAffixes.get().affixTierWeightBonus;

        List<String> selected = new ArrayList<>();
        Set<String> usedGroups = new HashSet<>();
        while (selected.size() < rank.affixes() && !pool.isEmpty()) {
            // Weighted pick: higher-tier affixes are more likely once unlocked
            double total = 0;
            for (Affix affix : pool) total += affixWeight(affix, bonus);
            double roll = mob.getRandom().nextDouble() * total;
            Affix picked = pool.getLast();
            for (Affix affix : pool) {
                roll -= affixWeight(affix, bonus);
                if (roll < 0) {
                    picked = affix;
                    break;
                }
            }
            pool.remove(picked);

            String group = picked.getExclusiveGroup();
            if (group != null && !usedGroups.add(group)) continue;
            selected.add(picked.getName());
        }

        return String.join(",", selected);
    }

    public static double affixWeight(Affix affix, float tierWeightBonus) {
        return 1.0 + tierWeightBonus * (affix.getMinTier() - 1);
    }

    private static void modifyAttribute(Mob entity, Holder<Attribute> attribute, float m) {
        var instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() * m);
        }
    }
}
