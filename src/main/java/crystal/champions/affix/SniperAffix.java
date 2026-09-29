package crystal.champions.affix;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.rpgdifficulty.mixin.access.AbstractArrowAccess;

/**
 * SniperAffix (skeletons)
 * Fires more slowly, but its arrows fly faster, go exactly where it aims and deal +50% damage.
 * Fire rate, speed and accuracy are in SniperMixin.
 */
public class SniperAffix extends MobSpecificAffix {
    public static final int MIN_ATTACK_INTERVAL = 60;
    public static final float ARROW_SPEED = 2.4F;
    public static final float DAMAGE_MULTIPLIER = 1.5F;

    public SniperAffix() {
        super("sniper", "skeletons", EntityTypes.SKELETON, mob -> mob instanceof AbstractSkeleton);
    }

    @Override
    public void onApply(Mob mob) {
        // Re-applies the bow cooldown now that the champion has this affix
        ((AbstractSkeleton) mob).reassessWeaponGoal();
    }

    @Override
    public void onProjectileSpawn(Mob owner, Projectile projectile) {
        if (projectile instanceof AbstractArrow arrow) {
            arrow.setBaseDamage(((AbstractArrowAccess) arrow).rpgdifficulty$getBaseDamage() * DAMAGE_MULTIPLIER);
        }
    }
}
