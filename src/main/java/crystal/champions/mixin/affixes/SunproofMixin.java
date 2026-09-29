package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sunproof: the champion never counts as standing in burning sunlight.
 */
@Mixin(Mob.class)
public class SunproofMixin {
    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void champions$sunproof(CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IChampions champion && champion.champions$hasAffix("sunproof")) {
            cir.setReturnValue(false);
        }
    }
}
