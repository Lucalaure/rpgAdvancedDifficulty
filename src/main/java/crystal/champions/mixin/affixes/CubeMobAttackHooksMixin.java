package crystal.champions.mixin.affixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import crystal.champions.affix.AffixEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Slimes and magma cubes hurt by contact instead of doHurtTarget, so on-hit affixes hook in here too.
 */
@Mixin(AbstractCubeMob.class)
public class CubeMobAttackHooksMixin {
    @WrapOperation(method = "dealDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean champions$onHurt(LivingEntity target, ServerLevel level, DamageSource source, float amount, Operation<Boolean> original) {
        boolean hurt = original.call(target, level, source, amount);
        if (hurt) {
            AffixEvents.onHurt((AbstractCubeMob) (Object) this, target);
        }
        return hurt;
    }
}
