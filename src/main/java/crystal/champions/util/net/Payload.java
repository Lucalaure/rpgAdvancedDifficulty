package crystal.champions.util.net;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public abstract class Payload implements CustomPacketPayload {
    private static final String CHAMPIONS = "champions";

    public record ChampionUpdate(UUID uuid, Component name, int tier, String affixes, float health, float maxHealth) implements CustomPacketPayload {
        public static final Type<ChampionUpdate> SERVER_UPDATE_ID = new Type<>(Identifier.fromNamespaceAndPath(CHAMPIONS, "update_hud"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ChampionUpdate> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, ChampionUpdate::uuid,
                ComponentSerialization.STREAM_CODEC, ChampionUpdate::name,
                ByteBufCodecs.VAR_INT, ChampionUpdate::tier,
                ByteBufCodecs.STRING_UTF8, ChampionUpdate::affixes,
                ByteBufCodecs.FLOAT, ChampionUpdate::health,
                ByteBufCodecs.FLOAT, ChampionUpdate::maxHealth,
                ChampionUpdate::new
        );
        @Override public Type<? extends CustomPacketPayload> type() { return SERVER_UPDATE_ID; }
    }


    public record ChampionUpdateCl(UUID uuid, Component name, int tier, String affixes, float health, float maxHealth) implements CustomPacketPayload {
        public static final Type<ChampionUpdateCl> CLIENT_UPDATE_ID = new Type<>(Identifier.fromNamespaceAndPath(CHAMPIONS, "update_client_hud"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ChampionUpdateCl> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, ChampionUpdateCl::uuid,
                ComponentSerialization.STREAM_CODEC, ChampionUpdateCl::name,
                ByteBufCodecs.VAR_INT, ChampionUpdateCl::tier,
                ByteBufCodecs.STRING_UTF8, ChampionUpdateCl::affixes,
                ByteBufCodecs.FLOAT, ChampionUpdateCl::health,
                ByteBufCodecs.FLOAT, ChampionUpdateCl::maxHealth,
                ChampionUpdateCl::new
        );
        @Override public Type<? extends CustomPacketPayload> type() { return CLIENT_UPDATE_ID; }
    }


    public record ChampionRemove(UUID uuid) implements CustomPacketPayload {
        public static final Type<ChampionRemove> REMOVE_ID = new Type<>(Identifier.fromNamespaceAndPath(CHAMPIONS, "remove_hud"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ChampionRemove> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, ChampionRemove::uuid,
                ChampionRemove::new
        );
        @Override public Type<? extends CustomPacketPayload> type() { return REMOVE_ID; }
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(Payload.ChampionUpdate.SERVER_UPDATE_ID, Payload.ChampionUpdate.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(Payload.ChampionUpdateCl.CLIENT_UPDATE_ID, Payload.ChampionUpdateCl.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(Payload.ChampionRemove.REMOVE_ID, Payload.ChampionRemove.CODEC);
    }
}
