package net.rpgdifficulty.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(Creeper.class)
public abstract class CreeperEntityMixin extends Monster {

    public CreeperEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    // Power already includes the champion tier radius boost, so the cap covers both mods combined
    @ModifyArg(method = "explodeCreeper", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;explode(Lnet/minecraft/world/entity/Entity;DDDFLnet/minecraft/world/level/Level$ExplosionInteraction;)V"), index = 4)
    private float explodeMixin(float power) {
        if (this.level() instanceof ServerLevel) {
            return Math.min(power * (float) MobStrengthener.getDamageFactor(this), RpgDifficultyMain.CONFIG.maxCreeperExplosionPower);
        }
        return power;
    }
}
