package net.rpgdifficulty.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(AgeableMob.class)
public abstract class PassiveEntityMixin extends PathfinderMob {

    public PassiveEntityMixin(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "ageBoundaryReached", at = @At(value = "HEAD"))
    protected void onGrowUpMixin(CallbackInfo info) {
        AttributeSupplier defaultAttributes = MobStrengthener.getDefaultAttributes(this);
        if (!this.level().isClientSide() && getAge() == 0
                && defaultAttributes != null
                && Math.abs(defaultAttributes.getBaseValue(Attributes.MAX_HEALTH)
                - this.getAttributeBaseValue(Attributes.MAX_HEALTH)) <= 0.0001D) {
            MobStrengthener.changeAttributes(this, (ServerLevel) this.level(), null, false);
        }
    }

    @Shadow
    public int getAge() {
        return 0;
    }
}
