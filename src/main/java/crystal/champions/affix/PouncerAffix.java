package crystal.champions.affix;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.phys.Vec3;

/**
 * PouncerAffix (spiders)
 * Leaps at its target from a distance.
 */
public class PouncerAffix extends MobSpecificAffix {
    private static final int COOLDOWN = 40;

    public PouncerAffix() {
        super("pouncer", "spiders", EntityTypes.SPIDER, mob -> mob instanceof Spider);
    }

    @Override
    public void onAttack(LivingEntity entity, Mob mob) {
        LivingEntity target = mob.getTarget();
        if (mob.tickCount % COOLDOWN != 0 || target == null || !target.isAlive() || !mob.onGround()) return;
        double distance = mob.distanceTo(target);
        if (distance < 4 || distance > 12 || !mob.hasLineOfSight(target)) return;

        Vec3 direction = target.position().subtract(mob.position()).multiply(1, 0, 1).normalize();
        double strength = Math.min(1.6, distance * 0.14);
        mob.setDeltaMovement(direction.x * strength, 0.55, direction.z * strength);
        mob.needsSync = true;
        mob.playSound(SoundEvents.SPIDER_AMBIENT, 1.0F, 0.7F);
    }
}
