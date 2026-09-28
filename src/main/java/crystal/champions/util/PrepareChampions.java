package crystal.champions.util;

import crystal.champions.affix.AffixRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static String prepareAffixes(ChampionRank rank) {
        List<String> pool = new ArrayList<>(AffixRegistry.ALL_AFFIXES.keySet());
        Collections.shuffle(pool);

        int count = Math.min(rank.affixes(), pool.size());
        List<String> selected = pool.subList(0, count);

        return String.join(",", selected);
    }

    private static void modifyAttribute(Mob entity, Holder<Attribute> attribute, float m) {
        var instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() * m);
        }
    }
}
