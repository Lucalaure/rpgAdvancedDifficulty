package crystal.champions.affix;

import crystal.champions.IChampions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.List;
import java.util.function.Predicate;

/**
 * Base for affixes limited to certain mobs. Subclasses pass which mobs can roll it,
 * the bestiary label key and the mob "/champion demo" spawns.
 */
public abstract class MobSpecificAffix extends Affix {
    private final Predicate<Mob> mobs;
    private final String mobsKey;
    private final EntityType<? extends Mob> exampleMob;

    protected MobSpecificAffix(String name, String mobsKey, EntityType<? extends Mob> exampleMob, Predicate<Mob> mobs) {
        super(name);
        this.mobs = mobs;
        this.mobsKey = "champions.bestiary.mobs." + mobsKey;
        this.exampleMob = exampleMob;
    }

    @Override
    public boolean canApplyTo(Mob mob) {
        return mobs.test(mob);
    }

    @Override
    public String getMobsKey() {
        return mobsKey;
    }

    @Override
    public EntityType<? extends Mob> getExampleMob() {
        return exampleMob;
    }

    /**
     * Summons normal (never-champion) mobs around the leader, e.g. Horde Caller, Coven and Warlord.
     * Each one is a random type from the list; they target the given entity if there is one.
     */
    protected static void spawnMinions(Mob leader, List<? extends EntityType<? extends Mob>> types, int count, @Nullable LivingEntity target) {
        if (!(leader.level() instanceof ServerLevel level)) return;
        for (int i = 0; i < count; i++) {
            EntityType<? extends Mob> type = types.get(leader.getRandom().nextInt(types.size()));
            Mob minion = type.create(level, EntitySpawnReason.REINFORCEMENT);
            if (minion == null) continue;

            double angle = leader.getRandom().nextDouble() * Math.PI * 2;
            minion.snapTo(leader.getX() + Math.cos(angle) * 2, leader.getY(), leader.getZ() + Math.sin(angle) * 2, leader.getRandom().nextFloat() * 360.0F, 0.0F);
            if (!level.noCollision(minion)) {
                minion.snapTo(leader.getX(), leader.getY(), leader.getZ(), leader.getYRot(), 0.0F);
            }
            minion.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(minion.position())), EntitySpawnReason.REINFORCEMENT, null);
            ((IChampions) minion).champions$setChampionTier(IChampions.NEVER_CHAMPION);
            if (target != null) {
                minion.setTarget(target);
            }
            level.addFreshEntity(minion);
        }
    }

    protected static void addBase(Mob mob, Holder<Attribute> attribute, double amount) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(Math.max(1.0, instance.getBaseValue() + amount));
        }
    }

    protected static void multiplyBase(Mob mob, Holder<Attribute> attribute, double factor) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() * factor);
        }
    }
}
