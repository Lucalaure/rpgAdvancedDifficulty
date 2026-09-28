package crystal.champions.mixin;

import crystal.champions.Champions;
import crystal.champions.IChampions;
import crystal.champions.bestiary.Bestiary;
import crystal.champions.util.net.ChampionsNetworking;
import crystal.champions.util.net.Payload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

/**
 * Боссбар на сервере / клиенте
 * Если сервер то рендерим тут
 * Если клиент отправляем пакеты с данными
 */
@Mixin(Mob.class)
public abstract class ServerUpdatePackets extends LivingEntity implements IChampions {

    protected ServerUpdatePackets(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Unique private final Set<UUID> trackedPlayerIds = new HashSet<>();

    /**
     * Сделал так, чтобы отправлял разные пакеты, и с ними уже можно делать хоть что-то
     * При наведении чтобы можно было видеть дальше
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void manageChampionHud(CallbackInfo ci) {
        if (this.level().isClientSide() || this.champions$getChampionTier() <= 0) return;

        Set<UUID> currentIds = new HashSet<>();
        Mob mob = (Mob) (Object) this;
        final boolean BOSSES = mob instanceof EnderDragon || mob instanceof WitherBoss;

        List<ServerPlayer> nearby = this.level().getEntitiesOfClass(
                ServerPlayer.class, this.getBoundingBox().inflate(80.0), p -> true
        );

        for (ServerPlayer player : nearby) {
            if (!ServerPlayNetworking.canSend(player, Payload.ChampionUpdate.SERVER_UPDATE_ID)) return;

            final UUID uuid = player.getUUID();
            final double distance = player.distanceToSqr(this);

            if (!BOSSES) {
                if (distance <= 1600) {
                    if (distance <= 225.0) {
                        ChampionsNetworking.sendUpdateS(player, mob, champions$getChampionTier(), champions$getAffixesString());
                        Bestiary.discover(player, champions$getAffixesString());
                        trackedPlayerIds.add(uuid);
                        currentIds.add(uuid);
                    }
                    ChampionsNetworking.sendUpdateC(player, mob, champions$getChampionTier(), champions$getAffixesString());
                }
            } else {
                ChampionsNetworking.sendUpdateS(player, mob, champions$getChampionTier(), champions$getAffixesString());
                Bestiary.discover(player, champions$getAffixesString());
                ChampionsNetworking.sendUpdateC(player, mob, champions$getChampionTier(), champions$getAffixesString());
                trackedPlayerIds.add(uuid);
                currentIds.add(uuid);
            }
        }
        removeIterator(currentIds, mob);
    }

    @Unique
    private void removeIterator(Set<UUID> currentIds, Mob mob) {
        Iterator<UUID> it = trackedPlayerIds.iterator();
        while (it.hasNext()) {
            UUID id = it.next();
            if (!currentIds.contains(id)) {
                try {
                    ServerPlayer player = Objects.requireNonNull(this.level().getServer()).getPlayerList().getPlayer(id);
                    if (player != null) {
                        ChampionsNetworking.sendRemove(player, mob);
                    }
                } catch (Exception e) {
                    Champions.LOGGER.error("Failed to remove player uuid");
                }
                it.remove();
            }
        }
    }
    /**
     * Removers
     * Подчищаем и удаляем все тут с баров
     * Ну и сами бары
     */
    @Unique
    private void removeIt() {
        if (this.level().getServer() == null) return;

        Set<UUID> ids= new HashSet<>(trackedPlayerIds);
        for (UUID id : ids) {
            ServerPlayer player = this.level().getServer().getPlayerList().getPlayer(id);
            Mob mob = (Mob) (Object) this;
            if (player != null && mob.isDeadOrDying()) {
                ChampionsNetworking.sendRemove(player, mob);
            }
        }
        trackedPlayerIds.clear();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        removeIt();
    }
}