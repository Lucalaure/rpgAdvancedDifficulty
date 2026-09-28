package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ShieldingMixin {
    /**
     * Отменяем урон если есть shield
     *             cir.setReturnValue(false);
     */
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void applyShielding(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IChampions champion && champion.champions$isShielding()) {
            cir.setReturnValue(false);
        }
    }
}
