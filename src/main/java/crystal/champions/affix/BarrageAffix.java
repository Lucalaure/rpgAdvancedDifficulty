package crystal.champions.affix;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

/**
 * BarrageAffix (blazes and ghasts)
 * Every 4 seconds unleashes an extra burst: 5 small fireballs from a blaze, 2 more big ones from a ghast.
 */
public class BarrageAffix extends MobSpecificAffix {
    private static final int COOLDOWN = 80;

    public BarrageAffix() {
        super("barrage", "blazes_ghasts", EntityTypes.BLAZE, mob -> mob instanceof Blaze || mob instanceof Ghast);
    }

    @Override
    public void onAttack(LivingEntity entity, Mob mob) {
        LivingEntity target = mob.getTarget();
        if (mob.tickCount % COOLDOWN != 0 || target == null || !target.isAlive()) return;
        if (!(mob.level() instanceof ServerLevel level) || mob.distanceTo(target) > 32 || !mob.hasLineOfSight(target)) return;

        if (mob instanceof Ghast ghast) {
            Vec3 view = ghast.getViewVector(1.0F);
            for (int i = 0; i < 2; i++) {
                Vec3 direction = new Vec3(
                        target.getX() - (ghast.getX() + view.x * 4.0) + ghast.getRandom().triangle(0, 3),
                        target.getY(0.5) - (0.5 + ghast.getY(0.5)),
                        target.getZ() - (ghast.getZ() + view.z * 4.0) + ghast.getRandom().triangle(0, 3));
                LargeFireball fireball = new LargeFireball(level, ghast, direction.normalize(), 1);
                fireball.setPos(ghast.getX() + view.x * 4.0, ghast.getY(0.5) + 0.5, ghast.getZ() + view.z * 4.0);
                level.addFreshEntity(fireball);
            }
            mob.playSound(SoundEvents.GHAST_SHOOT, 3.0F, 1.2F);
        } else {
            double xd = target.getX() - mob.getX();
            double yd = target.getY(0.5) - mob.getY(0.5);
            double zd = target.getZ() - mob.getZ();
            double spread = Math.sqrt(Math.sqrt(mob.distanceToSqr(target))) * 0.5;
            for (int i = 0; i < 5; i++) {
                Vec3 direction = new Vec3(mob.getRandom().triangle(xd, 2.297 * spread), yd, mob.getRandom().triangle(zd, 2.297 * spread));
                SmallFireball fireball = new SmallFireball(level, mob, direction.normalize());
                fireball.setPos(fireball.getX(), mob.getY(0.5) + 0.5, fireball.getZ());
                level.addFreshEntity(fireball);
            }
            mob.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.2F);
        }
    }
}
