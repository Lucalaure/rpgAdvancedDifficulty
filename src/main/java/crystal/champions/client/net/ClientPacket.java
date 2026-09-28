package crystal.champions.client.net;

import crystal.champions.util.net.Payload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientPacket {
    private ClientPacket() {
        /* This utility class should not be instantiated */
    }

    public static final Map<UUID, ChampionDisplayInfo> activeChampions = new ConcurrentHashMap<>();
    public static final Map<UUID, ChampionDisplayInfo> activeChampionsCl = new ConcurrentHashMap<>();
    /**
     * Сетевые пакеты
     * Регистрация
     */
    public static void registerPackets() {

        ClientPlayNetworking.registerGlobalReceiver(Payload.ChampionRemove.REMOVE_ID, (buf, context) -> {
            context.client().execute(() -> activeChampions.remove(buf.uuid()));
            context.client().execute(() -> activeChampionsCl.remove(buf.uuid()));
        });

        ClientPlayNetworking.registerGlobalReceiver(Payload.ChampionUpdateCl.CLIENT_UPDATE_ID, (buf, context) ->
                context.client().execute(() -> {
            ChampionDisplayInfo info = new ChampionDisplayInfo(
                    buf.name(),
                    buf.tier(),
                    buf.affixes(),
                    buf.health(),
                    buf.maxHealth(),
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
            );
            context.client().execute(() -> activeChampionsCl.put(buf.uuid(), info));
        }));

        ClientPlayNetworking.registerGlobalReceiver(Payload.ChampionUpdate.SERVER_UPDATE_ID, (buf, context) ->
                context.client().execute(() -> {
                ChampionDisplayInfo info = new ChampionDisplayInfo(
                        buf.name(),
                buf.tier(),
                buf.affixes(),
                buf.health(),
                buf.maxHealth(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis()
                );
                context.client().execute(() -> activeChampions.put(buf.uuid(), info));
        }));
    }
}