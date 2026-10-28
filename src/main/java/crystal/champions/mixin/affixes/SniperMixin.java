package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import crystal.champions.affix.SniperAffix;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sniper: slower bow cooldown, faster arrows and no aiming spread. (+50% damage is in SniperAffix.)
 */
@Mixin(AbstractSkeleton.class)
public class SniperMixin {
    @Shadow @Final private RangedBowAttackGoal<AbstractSkeleton> bowGoal;

    @Unique
    private boolean champions$isSniper() {
        return this instanceof IChampions champion && champion.champions$hasAffix("sniper");
    }

    @Inject(method = "reassessWeaponGoal", at = @At("TAIL"))
    private void champions$sniperFireRate(CallbackInfo ci) {
        if (champions$isSniper()) {
            this.bowGoal.setMinAttackInterval(SniperAffix.MIN_ATTACK_INTERVAL);
        }
    }

    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shoot(DDDFF)V"), index = 3)
    private float champions$sniperSpeed(float power) {
        return champions$isSniper() ? SniperAffix.ARROW_SPEED : power;
    }

    @ModifyArg(method = "performRangedAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shoot(DDDFF)V"), index = 4)
    private float champions$sniperAccuracy(float uncertainty) {
        return champions$isSniper() ? 0.0F : uncertainty;
    }
}
