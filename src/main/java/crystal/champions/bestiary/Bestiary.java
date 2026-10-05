package crystal.champions.bestiary;

import com.mojang.serialization.Codec;
import crystal.champions.ChampionAdvancements;
import crystal.champions.Champions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.rpgdifficulty.RpgDifficultyMain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Tracks which affixes each player has encountered. Saved on the player, kept on death,
 * and synced to that player's client so the bestiary book can show what they have found.
 */
public final class Bestiary {
    private Bestiary() {
    }

    public static final AttachmentType<List<String>> DISCOVERED_AFFIXES = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Champions.MOD_ID, "discovered_affixes"),
            builder -> builder
                    .initializer(List::of)
                    .persistent(Codec.STRING.listOf())
                    .copyOnDeath()
                    .syncWith(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), AttachmentSyncPredicate.targetOnly()));

    /** Whether the player has already been given their first bestiary book (only happens once). */
    public static final AttachmentType<Boolean> RECEIVED_BOOK = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Champions.MOD_ID, "received_bestiary"),
            builder -> builder.persistent(Codec.BOOL).copyOnDeath());

    /** Loads the attachment types during mod init. */
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            // New players start with a bestiary book; lost copies can be replaced with /bestiary
            if (!Boolean.TRUE.equals(player.getAttached(RECEIVED_BOOK))) {
                player.setAttached(RECEIVED_BOOK, true);
                BestiaryItem.give(player);
            }
            // In case the affix list changed (e.g. affixes disabled) since the player last discovered one
            ChampionAdvancements.checkBestiary(player);
        });
    }

    /** Called while the player is looking at a champion (see isLookingAt); unlocks any affixes they haven't seen yet. */
    public static void discover(ServerPlayer player, String affixes) {
        if (affixes.isEmpty()) return;

        List<String> known = player.getAttachedOrCreate(DISCOVERED_AFFIXES);
        List<String> added = new ArrayList<>();
        for (String name : affixes.split(",")) {
            if (!name.isEmpty() && !known.contains(name) && !added.contains(name)) {
                added.add(name);
            }
        }
        if (added.isEmpty()) return;

        List<String> updated = new ArrayList<>(known);
        updated.addAll(added);
        player.setAttached(DISCOVERED_AFFIXES, List.copyOf(updated));

        for (String name : added) {
            player.sendSystemMessage(Component.translatable("champions.bestiary.discovered", Component.translatable("affix." + name)), true);
        }
        ChampionAdvancements.checkBestiary(player);
    }

    /**
     * True if the player is aiming at the champion (crosshair on it), it's within the discovery range,
     * and no blocks are in the way. This is when its affixes count as encountered.
     */
    public static boolean isLookingAt(ServerPlayer player, Entity champion) {
        double range = RpgDifficultyMain.CONFIG.bestiaryDiscoveryRange;
        if (player.isSpectator() || player.distanceTo(champion) > range + champion.getBbWidth()) return false;

        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(range));
        // A little leeway, since the server's copy of the player's rotation lags slightly behind the client
        Optional<Vec3> hit = champion.getBoundingBox().inflate(0.3).clip(eye, end);
        if (hit.isEmpty()) return false;

        BlockHitResult blocked = player.level().clip(new ClipContext(eye, hit.get(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return blocked.getType() == HitResult.Type.MISS || blocked.getLocation().distanceToSqr(eye) >= hit.get().distanceToSqr(eye);
    }

    public static List<String> getDiscovered(Player player) {
        List<String> known = player.getAttached(DISCOVERED_AFFIXES);
        return known == null ? List.of() : known;
    }
}
