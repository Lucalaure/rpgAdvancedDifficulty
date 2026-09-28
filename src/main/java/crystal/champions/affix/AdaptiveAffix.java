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

    public float calculateDamage(LivingEntity entity, DamageSource source, float amount) {

        if (!(source.getEntity() instanceof LivingEntity)) return amount;
        IChampions champion = (IChampions) entity;
        Optional<ResourceKey<DamageType>> key = source.typeHolder().unwrapKey();
        if (key.isEmpty()) return amount;
        if (source.getDirectEntity() == champion&& source.getEntity() == champion) return amount;
        final String currentType = key.get().identifier().toString();

        final String lastType = champion.champions$getAdaptationType();
        final int count = champion.champions$getAdaptation();
        if (lastType.equals(currentType)) {
            champion.champions$setAdaptation(count + 1);
            final float reduction = amount * 0.15f * count;
            final float newAmount = amount - reduction;
            final float minDamage = amount * 0.2f;
            return Math.max(newAmount, minDamage);
        } else {
            champion.champions$setAdaptationType(currentType);
            champion.champions$setAdaptation(1);
            return amount;
        }
    }
}
