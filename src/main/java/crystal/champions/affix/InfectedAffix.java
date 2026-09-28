package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;

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
        if (entity.tickCount % config.timeBeforeInfected != 0) return;
        
        ServerLevel world = (ServerLevel) entity.level();

        List<Silverfish> nearby = world.getEntitiesOfClass(Silverfish.class, entity.getBoundingBox().inflate(40.0), e -> true);
        if (nearby.size() > config.maxSilverFishCount) return;

        final int count = (int) (entity.getHealth() * config.infectedFactorHealth + config.infectedSilverfish);
        final int maxCount = Math.min(count, config.maxSilverFishCount);

        for (int i = 0; i < maxCount; i++) {
            Silverfish silverfish = EntityTypes.SILVERFISH.create(world, EntitySpawnReason.EVENT);
            if (silverfish != null) {
                BlockPos pos = entity.blockPosition();

                silverfish.snapTo(entity.getX(), entity.getY(), entity.getZ(), entity.getRandom().nextFloat() * 360.0F, 0.0F);
                silverfish.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), EntitySpawnReason.EVENT, null);

                world.addFreshEntity(silverfish);
            }
        }
    }
}
