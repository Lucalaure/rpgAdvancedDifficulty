package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static crystal.champions.config.ChampionsConfigAffixes.get;

@Mixin(LivingEntity.class)
public class DampeningMixin {
    /**
     * Уменьшаем урон
     * Из важного
     *                  if (!source.isIn(DamageTypeTags.IS_PROJECTILE) || !source.isIn(DamageTypeTags.IS_FIRE)) return amount * 0.5f;
     * Просто source сравниваем и уменьшаем в 2 раза урон
     */
    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float applyDampening(float amount, DamageSource source) {
        if (this instanceof IChampions champion && champion.champions$getAffixesString().contains("dampening")
                && (!source.isIn(DamageTypeTags.IS_PROJECTILE)
                || !source.isIn(DamageTypeTags.IS_FIRE))) return amount * get().dampeningAmount;

        return amount;
    }
}
