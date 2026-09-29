package crystal.champions.affix;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Witch;

import java.util.List;

/**
 * CovenAffix (witches)
 * Arrives with 1-3 extra monsters, and every 2 seconds heals nearby hostile mobs.
 */
public class CovenAffix extends MobSpecificAffix {
    private static final int INTERVAL = 40;
    private static final float HEAL = 2.0F;
    private static final double RADIUS = 8.0;

    public CovenAffix() {
        super("coven", "witches", EntityTypes.WITCH, mob -> mob instanceof Witch);
    }

    private static final String SUMMONED_TAG = "champions.coven_summoned";
    private static final List<EntityType<? extends Mob>> FOLLOWERS = List.of(EntityTypes.ZOMBIE, EntityTypes.SKELETON, EntityTypes.SPIDER);

    @Override
    public void onTick(LivingEntity entity) {
        // First tick in the world: bring 1-3 followers (the tag is saved, so only once)
        if (entity instanceof Mob witch && witch.addTag(SUMMONED_TAG)) {
            spawnMinions(witch, FOLLOWERS, 1 + witch.getRandom().nextInt(3), witch.getTarget());
        }
        if (entity.tickCount % INTERVAL != 0 || !(entity.level() instanceof ServerLevel level)) return;
        for (Mob ally : level.getEntitiesOfClass(Mob.class, entity.getBoundingBox().inflate(RADIUS),
                mob -> mob != entity && mob instanceof Enemy && mob.isAlive() && mob.getHealth() < mob.getMaxHealth())) {
            ally.heal(HEAL);
            level.sendParticles(ParticleTypes.HEART, ally.getX(), ally.getY(1.0) + 0.3, ally.getZ(), 1, 0.2, 0.2, 0.2, 0.0);
        }
    }
}
