package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * InfectedAffix
 * Создаем чешуйниц от хп моба
 * При большом кол-ве отменяем спавн
 */
public class InfectedAffix extends Affix {

    public InfectedAffix() {
        super("infected");
    }

    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Override
    public void onTick(LivingEntity entity) {
        if (entity.age % config.timeBeforeInfected != 0) return;
        
        ServerWorld world = (ServerWorld) entity.getWorld();

        List<SilverfishEntity> nearby = world.getEntitiesByClass(SilverfishEntity.class, entity.getBoundingBox().expand(40.0), e -> true);
        if (nearby.size() > config.maxSilverFishCount) return;

        final int count = (int) (entity.getHealth() * config.infectedFactorHealth + config.infectedSilverfish);
        final int maxCount = Math.min(count, config.maxSilverFishCount);

        for (int i = 0; i < maxCount; i++) {
            SilverfishEntity silverfish = EntityType.SILVERFISH.create(world);
            if (silverfish != null) {
                BlockPos pos = entity.getBlockPos();

                silverfish.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getRandom().nextFloat() * 360.0F, 0.0F);
                silverfish.initialize(world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null);

                world.spawnEntity(silverfish);
            }
        }
    }
}
