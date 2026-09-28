package crystal.champions.affix;

import crystal.champions.IChampions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;

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

        if (!(source.getAttacker() instanceof LivingEntity)) return amount;
        IChampions champion = (IChampions) entity;
        Optional<RegistryKey<DamageType>> key = source.getTypeRegistryEntry().getKey();
        if (key.isEmpty()) return amount;
        if (source.getSource() == champion&& source.getAttacker() == champion) return amount;
        final String currentType = source.getTypeRegistryEntry().getKey().get().getValue().toString();

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
