package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import crystal.champions.affix.BerserkerAffix;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Berserker: the melee cooldown shrinks as the champion's health drops.
 */
@Mixin(MeleeAttackGoal.class)
public class BerserkerMixin {
    @Shadow @Final protected PathfinderMob mob;
    @Shadow private int ticksUntilNextAttack;

    @Inject(method = "resetAttackCooldown", at = @At("TAIL"))
    private void champions$berserkerCooldown(CallbackInfo ci) {
        if (this.mob instanceof IChampions champion && champion.champions$hasAffix("berserker")) {
            float multiplier = BerserkerAffix.cooldownMultiplier(this.mob.getHealth(), this.mob.getMaxHealth());
            this.ticksUntilNextAttack = Math.max(4, Math.round(this.ticksUntilNextAttack * multiplier));
        }
    }
}
