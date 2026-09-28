package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import crystal.champions.affix.AdaptiveAffix;
import crystal.champions.affix.AffixRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class AdaptiveMixin {
    /**
     * При ударе считаем урон для чемпиона
     *                 return affix.calculateDamage((LivingEntity)(Object)this, source, amount);
     */
    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float applyAdaptive(float amount, DamageSource source) {
        if (this instanceof IChampions champion && champion.champions$getAffixesString().contains("adaptive")) {
            AdaptiveAffix affix = (AdaptiveAffix) AffixRegistry.ALL_AFFIXES.get("adaptive");
            if (affix != null) {
                return affix.calculateDamage((LivingEntity) (Object) this, source, amount);
            }
        }
        return amount;
    }
}
