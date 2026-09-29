package crystal.champions.mixin;

import crystal.champions.IChampions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperEntityMixin extends Monster {
    @Shadow private int explosionRadius;

    @Shadow private int maxSwell;

    protected CreeperEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "explodeCreeper", at = @At("HEAD"))
    private void increaseExplosionRadius(CallbackInfo ci) {
        if (this instanceof IChampions champion) {
            int tier = champion.champions$getChampionTier();
            if (tier > 0) {
                this.explosionRadius = (int) (this.explosionRadius * (float) tier);
            }
        }
    }
    @Unique private boolean champions$fuseAdjusted = false;

    // Tier 4-5 creepers get a longer fuse (+10 ticks per tier above 3), applied once.
    // A creeper loaded from disk already has the longer fuse saved, so only the default fuse is extended.
    @Inject(method = "tick", at = @At("HEAD"))
    private void championsTick(CallbackInfo ci) {
        if (champions$fuseAdjusted) return;
        if (this instanceof IChampions champions && champions.champions$getChampionTier() > 3) {
            champions$fuseAdjusted = true;
            if (maxSwell == 30) {
                maxSwell = maxSwell + (champions.champions$getChampionTier() - 3) * 10;
            }
        }
    }
}
