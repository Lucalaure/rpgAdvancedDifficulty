package net.rpgadvanceddifficulty;

import crystal.champions.IChampions;
import crystal.champions.affix.UndyingAffix;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.rpgdifficulty.RpgDifficultyMain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Champions heal back to full once every player who hurt them has died.
 * Each champion remembers the players who damaged it; a player is crossed off
 * when they die. When nobody is left on the list, the champion is restored
 * (full health and Undying's second life) and the list starts over.
 */
public final class ChampionReset {
    /** Champion -> players who hurt it and haven't died since. Weak keys so unloaded champions don't leak. */
    private static final Map<LivingEntity, Set<UUID>> ATTACKERS = new WeakHashMap<>();

    private ChampionReset() {
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
            if (blocked || !RpgDifficultyMain.CONFIG.resetChampionsOnPlayerDeath) return;
            if (!isChampion(entity) || !entity.isAlive()) return;
            // getEntity() is the owner for projectiles, so arrows and tridents count too
            if (source.getEntity() instanceof ServerPlayer player) {
                ATTACKERS.computeIfAbsent(entity, e -> new HashSet<>()).add(player.getUUID());
            }
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayer player) {
                onPlayerDeath(player.getUUID());
            } else {
                ATTACKERS.remove(entity);
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> ATTACKERS.clear());
    }

    private static void onPlayerDeath(UUID playerId) {
        Iterator<Map.Entry<LivingEntity, Set<UUID>>> it = ATTACKERS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<LivingEntity, Set<UUID>> entry = it.next();
            Set<UUID> attackers = entry.getValue();
            if (!attackers.remove(playerId) || !attackers.isEmpty()) continue;
            it.remove();
            LivingEntity champion = entry.getKey();
            if (RpgDifficultyMain.CONFIG.resetChampionsOnPlayerDeath && champion.isAlive() && !champion.isRemoved()) {
                restore(champion);
            }
        }
    }

    private static void restore(LivingEntity champion) {
        champion.setHealth(champion.getMaxHealth());
        champion.removeTag(UndyingAffix.USED_TAG);
    }

    private static boolean isChampion(LivingEntity entity) {
        return entity instanceof IChampions champion && champion.champions$getChampionTier() > 0;
    }
}
