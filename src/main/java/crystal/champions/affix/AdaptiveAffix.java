package crystal.champions.affix;

import crystal.champions.IChampions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

/**
 * Адаптивный аффикс
 * Сравниваем damage source и если совпадает, то уменьшаем урон, если нет то сбрасываем
 */
public class AdaptiveAffix extends Affix {

    public AdaptiveAffix() {
        super("adaptive");
    }

    /** Damage after adaptation; does not change the adaptation state. */
    public float calculateDamage(LivingEntity entity, DamageSource source, float amount) {
        final String currentType = adaptationType(entity, source);
        if (currentType == null) return amount;

        IChampions champion = (IChampions) entity;
        final int count = champion.champions$getAdaptation();
        if (!champion.champions$getAdaptationType().equals(currentType)) return amount;

        final float reduction = amount * 0.15f * count;
        final float minDamage = amount * 0.2f;
        return Math.max(amount - reduction, minDamage);
    }

    /** Called only for hits that landed: same type in a row adapts further, a new type resets it. */
    public void recordHit(LivingEntity entity, DamageSource source) {
        final String currentType = adaptationType(entity, source);
        if (currentType == null) return;

        IChampions champion = (IChampions) entity;
        if (champion.champions$getAdaptationType().equals(currentType)) {
            champion.champions$setAdaptation(champion.champions$getAdaptation() + 1);
        } else {
            champion.champions$setAdaptationType(currentType);
            champion.champions$setAdaptation(1);
        }
    }

    private static String adaptationType(LivingEntity entity, DamageSource source) {
        if (!(source.getEntity() instanceof LivingEntity)) return null;
        if (source.getDirectEntity() == entity && source.getEntity() == entity) return null;
        Optional<ResourceKey<DamageType>> key = source.typeHolder().unwrapKey();
        return key.map(k -> k.identifier().toString()).orElse(null);
    }
}
