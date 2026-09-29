package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import crystal.champions.affix.AdaptiveAffix;
import crystal.champions.affix.AffixRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class AdaptiveMixin {
    /**
     * При ударе считаем урон для чемпиона
     *                 return affix.calculateDamage((LivingEntity)(Object)this, source, amount);
     */
    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
    private float applyAdaptive(float amount, ServerLevel level, DamageSource source) {
        if (this instanceof IChampions champion && champion.champions$getAffixesString().contains("adaptive")) {
            AdaptiveAffix affix = (AdaptiveAffix) AffixRegistry.ALL_AFFIXES.get("adaptive");
            if (affix != null) {
                return affix.calculateDamage((LivingEntity) (Object) this, source, amount);
            }
        }
        return amount;
    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void recordAdaptive(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && this instanceof IChampions champion && champion.champions$getAffixesString().contains("adaptive")) {
            AdaptiveAffix affix = (AdaptiveAffix) AffixRegistry.ALL_AFFIXES.get("adaptive");
            if (affix != null) {
                affix.recordHit((LivingEntity) (Object) this, source);
            }
        }
    }
}
