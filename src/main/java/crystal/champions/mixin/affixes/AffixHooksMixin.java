package crystal.champions.mixin.affixes;

import crystal.champions.affix.Affix;
import crystal.champions.affix.AffixEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Affix.onDamaged: the champion took a hit that landed.
 */
@Mixin(LivingEntity.class)
public class AffixHooksMixin {
    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void champions$onDamaged(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return;
        LivingEntity self = (LivingEntity) (Object) this;
        for (Affix affix : AffixEvents.affixes(self)) {
            affix.onDamaged(self, source, amount);
        }
    }
}
