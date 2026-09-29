package crystal.champions.affix;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Witch;

/**
 * CovenAffix (witches)
 * Every 2 seconds heals nearby hostile mobs.
 */
public class CovenAffix extends MobSpecificAffix {
    private static final int INTERVAL = 40;
    private static final float HEAL = 2.0F;
    private static final double RADIUS = 8.0;

    public CovenAffix() {
        super("coven", "witches", EntityTypes.WITCH, mob -> mob instanceof Witch);
    }

    @Override
    public void onTick(LivingEntity entity) {
        if (entity.tickCount % INTERVAL != 0 || !(entity.level() instanceof ServerLevel level)) return;
        for (Mob ally : level.getEntitiesOfClass(Mob.class, entity.getBoundingBox().inflate(RADIUS),
                mob -> mob != entity && mob instanceof Enemy && mob.isAlive() && mob.getHealth() < mob.getMaxHealth())) {
            ally.heal(HEAL);
            level.sendParticles(ParticleTypes.HEART, ally.getX(), ally.getY(1.0) + 0.3, ally.getZ(), 1, 0.2, 0.2, 0.2, 0.0);
        }
    }
}
