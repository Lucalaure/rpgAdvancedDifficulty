package crystal.champions.util.net;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Даем Identifier пакетам и отправляем их
 * Разделено на разные секции
 * sendUpdateS (server)
 * sendUpdateC (client)
 */
public class ChampionsNetworking {
    private ChampionsNetworking() {
        /* This utility class should not be instantiated */
    }

    public static void sendUpdateS(ServerPlayerEntity player, MobEntity entity, int tier, String affixes) {
        Payload.ChampionUpdate payload = new Payload.ChampionUpdate(
                entity.getUuid(),
                entity.getDisplayName(),
                tier,
                affixes,
                entity.getHealth(),
                entity.getMaxHealth()
        );
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendUpdateC(ServerPlayerEntity player, MobEntity entity, int tier, String affixes) {
        Payload.ChampionUpdateCl payload = new Payload.ChampionUpdateCl(
                entity.getUuid(),
                entity.getDisplayName(),
                tier,
                affixes,
                entity.getHealth(),
                entity.getMaxHealth()
        );
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendRemove(ServerPlayerEntity player, MobEntity mob) {
        ServerPlayNetworking.send(player, new Payload.ChampionRemove(mob.getUuid()));
    }
}
