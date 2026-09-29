package crystal.champions.mixin.affixes;

import crystal.champions.affix.AffixEvents;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Affix.onProjectileHit: a projectile fired by a champion hit an entity or block.
 */
@Mixin(Projectile.class)
public class ProjectileHooksMixin {
    @Inject(method = "onHit", at = @At("HEAD"))
    private void champions$onProjectileHit(HitResult hitResult, CallbackInfo ci) {
        AffixEvents.onProjectileHit((Projectile) (Object) this, hitResult);
    }
}
