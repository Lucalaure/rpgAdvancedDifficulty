package crystal.champions.util;

import crystal.champions.affix.AffixRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrepareChampions {
    private PrepareChampions() {
        /* This utility class should not be instantiated */
    }

    public static void prepareAttributes(MobEntity mob, ChampionRank rank) {
        final float h = rank.growth_h();
        final float s = rank.growth_s();
        modifyAttribute(mob, EntityAttributes.GENERIC_MAX_HEALTH, h);
        mob.setHealth(mob.getMaxHealth());

        if (mob.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE) != null) {
            modifyAttribute(mob, EntityAttributes.GENERIC_ATTACK_DAMAGE, s);
        }
    }

    public static String prepareAffixes(ChampionRank rank) {
        List<String> pool = new ArrayList<>(AffixRegistry.ALL_AFFIXES.keySet());
        Collections.shuffle(pool);

        int count = Math.min(rank.affixes(), pool.size());
        List<String> selected = pool.subList(0, count);

        return String.join(",", selected);
    }

    private static void modifyAttribute(MobEntity entity, RegistryEntry<EntityAttribute> attribute, float m) {
        var instance = entity.getAttributeInstance(attribute);
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() * m);
        }
    }
}
