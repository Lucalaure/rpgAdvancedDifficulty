package crystal.champions.mixin.affixes;

import crystal.champions.affix.AffixEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Affix.onHurt: the champion's melee attack landed (not blocked, not during invulnerability).
 * Replaces the old per-affix mixins for Knocking, Blinded and Paralyzing.
 */
@Mixin(Mob.class)
public class MobAttackHooksMixin {
    @Inject(method = "doHurtTarget", at = @At("RETURN"))
    private void champions$onHurt(ServerLevel level, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && target instanceof LivingEntity livingTarget) {
            AffixEvents.onHurt((Mob) (Object) this, livingTarget);
        }
    }
}
