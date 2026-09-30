package net.rpgadvanceddifficulty;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Sends each player the difficulty at their position once a second, for the F3 readout.
 * Calculated on the server so zones, dimension datapacks and server config are all accounted for.
 */
public final class DifficultySync {
    private DifficultySync() {
    }

    public record DifficultyInfo(float factor, float maxFactor, float championChance, int minutesToNext, boolean inZone) implements CustomPacketPayload {
        public static final Type<DifficultyInfo> TYPE = new Type<>(Identifier.fromNamespaceAndPath("rpgadvanceddifficulty", "difficulty_info"));
        public static final StreamCodec<RegistryFriendlyByteBuf, DifficultyInfo> CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, DifficultyInfo::factor,
                ByteBufCodecs.FLOAT, DifficultyInfo::maxFactor,
                ByteBufCodecs.FLOAT, DifficultyInfo::championChance,
                ByteBufCodecs.VAR_INT, DifficultyInfo::minutesToNext,
                ByteBufCodecs.BOOL, DifficultyInfo::inZone,
                DifficultyInfo::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(DifficultyInfo.TYPE, DifficultyInfo.CODEC);
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 != 0) return;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (!ServerPlayNetworking.canSend(player, DifficultyInfo.TYPE)) continue;
                DifficultyReport.Snapshot snapshot = DifficultyReport.compute(player.level(), player.position());
                ServerPlayNetworking.send(player, new DifficultyInfo(snapshot.factor(), snapshot.maxFactor(),
                        snapshot.championChance(), snapshot.minutesToNext(), snapshot.inZone()));
            }
        });
    }
}
