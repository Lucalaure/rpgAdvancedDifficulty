package crystal.champions.affix;

import crystal.champions.IChampions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

import java.util.List;

/**
 * Dispatches game events to a champion's affixes. Called from the hook mixins in mixin/affixes.
 */
public final class AffixEvents {
    private AffixEvents() {
    }

    public static List<Affix> affixes(Entity entity) {
        return entity instanceof IChampions champion ? champion.champions$getActiveAffixes() : List.of();
    }

    public static void onHurt(LivingEntity champion, LivingEntity target) {
        for (Affix affix : affixes(champion)) {
            affix.onHurt(champion, target);
        }
    }

    public static void onDeath(LivingEntity champion, net.minecraft.world.damagesource.DamageSource source) {
        if (champion.level().isClientSide()) return;
        for (Affix affix : affixes(champion)) {
            affix.onDeath(champion, source);
        }
    }

    public static void onProjectileSpawn(Projectile projectile) {
        if (projectile.level().isClientSide() || !(projectile.getOwner() instanceof Mob owner)) return;
        for (Affix affix : affixes(owner)) {
            affix.onProjectileSpawn(owner, projectile);
        }
    }

    public static void onProjectileHit(Projectile projectile, HitResult hit) {
        if (projectile.level().isClientSide() || !(projectile.getOwner() instanceof Mob owner)) return;
        for (Affix affix : affixes(owner)) {
            affix.onProjectileHit(owner, projectile, hit);
        }
    }
}
