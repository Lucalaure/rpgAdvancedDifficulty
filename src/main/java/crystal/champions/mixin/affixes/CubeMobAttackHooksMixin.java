package crystal.champions.mixin.affixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import crystal.champions.affix.AffixEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Slimes and magma cubes hurt by contact instead of doHurtTarget, so on-hit affixes hook in here too.
 */
@Mixin(Slime.class)
public class CubeMobAttackHooksMixin {
    @WrapOperation(method = "dealDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean champions$onHurt(LivingEntity target, DamageSource source, float amount, Operation<Boolean> original) {
        boolean hurt = original.call(target, source, amount);
        if (hurt) {
            AffixEvents.onHurt((Slime) (Object) this, target);
        }
        return hurt;
    }
}
