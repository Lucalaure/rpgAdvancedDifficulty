package crystal.champions.affix;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.Stray;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * FrostArcherAffix (strays)
 * Its arrows freeze whatever they hit, like standing in powder snow.
 */
public class FrostArcherAffix extends MobSpecificAffix {
    private static final String FROST_TAG = "champions.frost_arrow";
    /** Extra ticks past fully frozen: stays frozen for about 4 seconds while thawing. */
    private static final int EXTRA_FROZEN_TICKS = 160;

    public FrostArcherAffix() {
        super("frost_archer", "strays", EntityTypes.STRAY, mob -> mob instanceof Stray);
    }

    @Override
    public void onProjectileSpawn(Mob owner, Projectile projectile) {
        if (projectile instanceof AbstractArrow) {
            projectile.addTag(FROST_TAG);
        }
    }

    @Override
    public void onProjectileHit(Mob owner, Projectile projectile, HitResult hit) {
        if (projectile.entityTags().contains(FROST_TAG) && hit instanceof EntityHitResult entityHit
                && entityHit.getEntity() instanceof LivingEntity target && target.canFreeze()) {
            target.setTicksFrozen(Math.max(target.getTicksFrozen(), target.getTicksRequiredToFreeze() + EXTRA_FROZEN_TICKS));
        }
    }
}
