package crystal.champions.util.net;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;

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

    public static void sendUpdateS(ServerPlayer player, Mob entity, int tier, String affixes) {
        Payload.ChampionUpdate payload = new Payload.ChampionUpdate(
                entity.getUUID(),
                entity.getDisplayName(),
                tier,
                affixes,
                entity.getHealth(),
                entity.getMaxHealth()
        );
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendUpdateC(ServerPlayer player, Mob entity, int tier, String affixes) {
        Payload.ChampionUpdateCl payload = new Payload.ChampionUpdateCl(
                entity.getUUID(),
                entity.getDisplayName(),
                tier,
                affixes,
                entity.getHealth(),
                entity.getMaxHealth()
        );
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendRemove(ServerPlayer player, Mob mob) {
        ServerPlayNetworking.send(player, new Payload.ChampionRemove(mob.getUUID()));
    }
}
