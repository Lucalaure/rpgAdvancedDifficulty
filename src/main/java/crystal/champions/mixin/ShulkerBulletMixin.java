package crystal.champions.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import crystal.champions.IBullet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBullet.class)
public abstract class ShulkerBulletMixin extends Entity implements IBullet {
    @Unique private static final EntityDataAccessor<Boolean> ARCTIC = SynchedEntityData.defineId(ShulkerBulletMixin.class, EntityDataSerializers.BOOLEAN);
    @Unique private static final EntityDataAccessor<Boolean> MOLTEN = SynchedEntityData.defineId(ShulkerBulletMixin.class, EntityDataSerializers.BOOLEAN);

    protected ShulkerBulletMixin(EntityType<?> type, Level world) { super(type, world); }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void initChampionTracker(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(ARCTIC, false);
        builder.define(MOLTEN, false);
    }

    @Override
    public void champions$setArctic(boolean arctic) {
        this.entityData.set(ARCTIC, arctic);
    }

    @Override
    public boolean champions$isArctic() {
        return this.entityData.get(ARCTIC);
    }

    @Override
    public void champions$setMolten(boolean molten) {
        this.entityData.set(MOLTEN, molten);
    }

    @Override
    public boolean champions$isMolten() {
        return this.entityData.get(MOLTEN);
    }

    // NBT оставляем только для сохранения в файл (чтобы после перезахода в мир пули не теряли тип)
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeChampionData(ValueOutput nbt, CallbackInfo ci) {
        nbt.putBoolean("arctic", champions$isArctic());
        nbt.putBoolean("molten", champions$isMolten());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readChampionData(ValueInput nbt, CallbackInfo ci) {
        champions$setArctic(nbt.getBooleanOr("arctic", false));
        champions$setMolten(nbt.getBooleanOr("molten", false));
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void championEffect(EntityHitResult entityHitResult, CallbackInfo ci) {
        Entity targetEntity = entityHitResult.getEntity();
        if (!(targetEntity instanceof LivingEntity target)) return;

        if (champions$isArctic()) {
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 5));
            ci.cancel();
        }
        if (champions$isMolten()) {
            target.igniteForSeconds(5);
            ci.cancel();
        }
    }
    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
            )
    )
    private void redirectParticles(Level world, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original) {
        if (champions$isArctic()) {
            world.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, 0, 0);
        } else if (champions$isMolten()) {
            if (this.tickCount % 2 == 0) world.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
            world.addParticle(ParticleTypes.WHITE_ASH, x, y, z, 0, 0.2, 0);
        } else {
            original.call(world, parameters, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}
