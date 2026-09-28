package crystal.champions.mixin;

import crystal.champions.IChampions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public class CreeperEntityMixin extends HostileEntity {
    @Shadow private int explosionRadius;

    @Shadow private int fuseTime = 30;

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "explode", at = @At("HEAD"))
    private void increaseExplosionRadius(CallbackInfo ci) {
        if (this instanceof IChampions champion) {
            int tier = champion.champions$getChampionTier();
            if (tier > 0) {
                this.explosionRadius = (int) (this.explosionRadius * (float) tier);
            }
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void championsTick(CallbackInfo ci) {
        if (this instanceof IChampions champions && champions.champions$getChampionTier() > 3) {
                fuseTime = fuseTime + (champions.champions$getChampionTier() - 3) * 10;
        }
    }
}
