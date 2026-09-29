package crystal.champions.mixin.affixes;

import crystal.champions.affix.AffixEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Affix.onProjectileSpawn: a champion's projectile is added to the world (already aimed and launched).
 */
@Mixin(ServerLevel.class)
public class ProjectileSpawnHooksMixin {
    @Inject(method = "addFreshEntity", at = @At("HEAD"))
    private void champions$onProjectileSpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Projectile projectile) {
            AffixEvents.onProjectileSpawn(projectile);
        }
    }
}
