package net.rpgdifficulty.mixin;

import net.minecraft.server.level.ServerLevel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.rpgdifficulty.access.EntityAccess;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity implements EntityAccess {

    @Unique
    private float mobHealthMultiplier = 1.0f;
    @Unique
    private boolean strengthened = false;

    public MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "getBaseExperienceReward", at = @At(value = "RETURN", ordinal = 0))
    private int getXpToDropMixin(int original) {
        return MobStrengthener.getXpToDropAddition((Mob) (Object) this, (ServerLevel) this.level(), original);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(ValueInput input, CallbackInfo info) {
        this.mobHealthMultiplier = input.getFloatOr("MobHealthMultiplier", 0.0f);
        this.strengthened = input.getBooleanOr("RpgDifficultyApplied", false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(ValueOutput output, CallbackInfo info) {
        output.putFloat("MobHealthMultiplier", this.mobHealthMultiplier);
        output.putBoolean("RpgDifficultyApplied", this.strengthened);
    }

    @Override
    public void setMobHealthMultiplier(float multiplier) {
        this.mobHealthMultiplier = multiplier;
    }

    @Override
    public float getMobHealthMultiplier() {
        return this.mobHealthMultiplier;
    }

    @Override
    public void setStrengthened(boolean strengthened) {
        this.strengthened = strengthened;
    }

    @Override
    public boolean isStrengthened() {
        return this.strengthened;
    }

}
