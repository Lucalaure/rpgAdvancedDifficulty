package crystal.champions.affix;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * VolleyAffix (skeletons)
 * Every few seconds fires a spread of 3 arrows at its target.
 */
public class VolleyAffix extends MobSpecificAffix {
    private static final int COOLDOWN = 60;
    private static final double SPREAD = 0.2; // radians between arrows

    public VolleyAffix() {
        super("volley", "skeletons", EntityTypes.SKELETON, mob -> mob instanceof AbstractSkeleton);
    }

    @Override
    public void onAttack(LivingEntity entity, Mob mob) {
        LivingEntity target = mob.getTarget();
        if (mob.tickCount % COOLDOWN != 0 || target == null || !target.isAlive()) return;
        if (!(mob.level() instanceof ServerLevel level) || mob.distanceTo(target) > 24 || !mob.hasLineOfSight(target)) return;

        ItemStack arrowStack = new ItemStack(Items.ARROW);
        for (int i = -1; i <= 1; i++) {
            AbstractArrow arrow = ProjectileUtil.getMobArrow(mob, arrowStack, 1.0F, null);
            double xd = target.getX() - mob.getX();
            double zd = target.getZ() - mob.getZ();
            double yd = target.getY(1.0 / 3.0) - arrow.getY();
            double distance = Math.sqrt(xd * xd + zd * zd);
            double angle = i * SPREAD;
            double rx = xd * Math.cos(angle) - zd * Math.sin(angle);
            double rz = xd * Math.sin(angle) + zd * Math.cos(angle);
            Projectile.spawnProjectileUsingShoot(arrow, level, arrowStack, rx, yd + distance * 0.2, rz, 1.6F, 2.0F);
        }
        mob.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.2F);
    }
}
