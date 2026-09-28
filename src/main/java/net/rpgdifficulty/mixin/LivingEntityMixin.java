package net.rpgdifficulty.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.api.MobStrengthener;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    // Could check for damagesource and increase the damage amount
    // Warden sonic boom is in lambda at sonic boom task class
    // @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z"))
    // private void damageMixin(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
    // System.out.println(source + " : " + source.getEntity());
    // }

    // Death loot path (1.21.1 "dropLoot"). The loot params are rebuilt exactly like vanilla does in
    // dropFromLootTable(ServerLevel, DamageSource, boolean, ResourceKey, Consumer) instead of capturing locals.
    @Inject(method = "dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;Z)V", at = @At("TAIL"))
    protected void dropLootMixin(ServerLevel level, DamageSource source, boolean playerKilled, CallbackInfo info) {
        if (RpgDifficultyMain.CONFIG.dropMoreLoot && (Object) this instanceof Mob mobEntity) {
            Optional<ResourceKey<LootTable>> lootTableKey = mobEntity.getLootTable();
            if (lootTableKey.isEmpty()) {
                return;
            }
            LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableKey.get());
            LootParams.Builder builder = new LootParams.Builder(level)
                    .withParameter(LootContextParams.THIS_ENTITY, mobEntity)
                    .withParameter(LootContextParams.ORIGIN, mobEntity.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                    .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, source.getEntity())
                    .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, source.getDirectEntity());
            Player killerPlayer = mobEntity.getLastHurtByPlayer();
            if (playerKilled && killerPlayer != null) {
                builder = builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, killerPlayer).withLuck(killerPlayer.getLuck());
            }
            MobStrengthener.dropMoreLoot(mobEntity, level, lootTable, builder.create(LootContextParamSets.ENTITY));
        }
    }

}
