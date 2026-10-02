package crystal.champions;

import crystal.champions.affix.AffixRegistry;
import crystal.champions.bestiary.Bestiary;
import crystal.champions.util.ChampionRank;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Awards the champion advancements (data/champions/advancement), which sit after "Monster Hunter"
 * in the Adventure tab: Champion Slayer -> Apex Hunter -> Know Thy Enemy.
 * Each has a single "impossible" criterion that's granted from here.
 */
public final class ChampionAdvancements {
    private ChampionAdvancements() {
    }

    public static final Identifier CHAMPION_SLAYER = Identifier.fromNamespaceAndPath(Champions.MOD_ID, "champion_slayer");
    public static final Identifier APEX_HUNTER = Identifier.fromNamespaceAndPath(Champions.MOD_ID, "apex_hunter");
    public static final Identifier KNOW_THY_ENEMY = Identifier.fromNamespaceAndPath(Champions.MOD_ID, "know_thy_enemy");

    /** A champion died: whoever killed it (directly or with a projectile) gets the kill advancements. */
    public static void onChampionKilled(LivingEntity champion, DamageSource source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) return;
        int tier = ((IChampions) champion).champions$getChampionTier();
        if (tier <= 0) return;

        award(player, CHAMPION_SLAYER);
        if (tier >= ChampionRank.RANKS.getLast().tier()) {
            award(player, APEX_HUNTER);
        }
    }

    /** Called when the player's bestiary changes (and on join): every enabled affix discovered. */
    public static void checkBestiary(ServerPlayer player) {
        List<String> known = Bestiary.getDiscovered(player);
        if (!AffixRegistry.ALL_AFFIXES.isEmpty() && known.containsAll(AffixRegistry.ALL_AFFIXES.keySet())) {
            award(player, KNOW_THY_ENEMY);
        }
    }

    private static void award(ServerPlayer player, Identifier id) {
        AdvancementHolder advancement = player.level().getServer().getAdvancements().get(id);
        if (advancement != null) {
            player.getAdvancements().award(advancement, "requirement");
        }
    }
}
