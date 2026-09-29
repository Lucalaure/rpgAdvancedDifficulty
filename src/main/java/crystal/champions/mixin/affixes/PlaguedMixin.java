package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Plagued: the champion can't be poisoned (by its own cloud or anything else).
 */
@Mixin(LivingEntity.class)
public class PlaguedMixin {
    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    private void champions$poisonImmune(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if (effect.is(MobEffects.POISON) && this instanceof IChampions champion && champion.champions$hasAffix("plagued")) {
            cir.setReturnValue(false);
        }
    }
}
