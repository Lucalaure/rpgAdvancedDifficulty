package crystal.champions.affix;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/**
 * StormcallerAffix (any champion)
 * Every 6 seconds, 1-3 lightning bolts strike around its target. The champion is immune to lightning,
 * and bolts never land right next to it (so e.g. creepers don't charge themselves).
 */
public class StormcallerAffix extends Affix {
    private static final int COOLDOWN = 120;
    private static final double MIN_DISTANCE_FROM_CHAMPION = 4.0;

    public StormcallerAffix() {
        super("stormcaller");
    }

    @Override
    public void onAttack(LivingEntity entity, Mob mob) {
        LivingEntity target = mob.getTarget();
        if (mob.tickCount % COOLDOWN != 0 || target == null || !target.isAlive()) return;
        if (!(mob.level() instanceof ServerLevel level) || mob.distanceTo(target) > 24) return;

        int bolts = 1 + mob.getRandom().nextInt(3);
        for (int i = 0, attempts = 0; i < bolts && attempts < 12; attempts++) {
            double angle = mob.getRandom().nextDouble() * Math.PI * 2;
            double radius = 1.5 + mob.getRandom().nextDouble() * 3.5;
            BlockPos column = BlockPos.containing(target.getX() + Math.cos(angle) * radius, target.getY(), target.getZ() + Math.sin(angle) * radius);
            BlockPos ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, column);
            Vec3 strike = Vec3.atBottomCenterOf(ground);
            if (strike.distanceTo(mob.position()) < MIN_DISTANCE_FROM_CHAMPION) continue;

            LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
            if (bolt == null) continue;
            bolt.snapTo(strike);
            level.addFreshEntity(bolt);
            i++;
        }
    }
}
