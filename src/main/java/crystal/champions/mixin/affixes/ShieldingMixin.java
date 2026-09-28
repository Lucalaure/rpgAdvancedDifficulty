package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
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
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void applyShielding(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IChampions champion && champion.champions$isShielding()) {
            cir.setReturnValue(false);
        }
    }
}
