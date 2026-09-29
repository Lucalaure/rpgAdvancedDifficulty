package crystal.champions.affix;

import crystal.champions.IChampions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.spider.Spider;

/**
 * BroodMotherAffix (spiders, not cave spiders)
 * Spawns cave spiders when damaged, up to a limit nearby.
 */
public class BroodMotherAffix extends MobSpecificAffix {
    private static final int PER_HIT = 2;
    private static final int MAX_NEARBY = 6;

    public BroodMotherAffix() {
        super("brood_mother", "spiders", EntityTypes.SPIDER, mob -> mob instanceof Spider && !(mob instanceof CaveSpider));
    }

    @Override
    public void onDamaged(LivingEntity champion, DamageSource source, float amount) {
        if (!(champion.level() instanceof ServerLevel level) || !champion.isAlive()) return;
        int nearby = level.getEntitiesOfClass(CaveSpider.class, champion.getBoundingBox().inflate(16)).size();

        for (int i = 0; i < Math.min(PER_HIT, MAX_NEARBY - nearby); i++) {
            CaveSpider spider = EntityTypes.CAVE_SPIDER.create(level, EntitySpawnReason.MOB_SUMMONED);
            if (spider == null) continue;
            spider.snapTo(champion.getX(), champion.getY(), champion.getZ(), champion.getRandom().nextFloat() * 360.0F, 0.0F);
            spider.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(champion.position())), EntitySpawnReason.MOB_SUMMONED, null);
            ((IChampions) spider).champions$setChampionTier(IChampions.NEVER_CHAMPION);
            if (source.getEntity() instanceof LivingEntity attacker && attacker != champion) {
                spider.setTarget(attacker);
            }
            level.addFreshEntity(spider);
        }
    }
}
