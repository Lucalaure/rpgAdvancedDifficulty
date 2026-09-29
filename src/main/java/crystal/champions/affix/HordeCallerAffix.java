package crystal.champions.affix;

import crystal.champions.IChampions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;

/**
 * HordeCallerAffix (zombies)
 * The first time it targets a player, 2-3 normal zombies of its kind join the fight.
 */
public class HordeCallerAffix extends MobSpecificAffix {
    private static final String CALLED_TAG = "champions.horde_called";

    public HordeCallerAffix() {
        super("horde_caller", "zombies", EntityTypes.ZOMBIE, mob -> mob instanceof Zombie);
    }

    @Override
    public void onAttack(LivingEntity entity, Mob mob) {
        if (!(mob.getTarget() instanceof Player player) || !(mob.level() instanceof ServerLevel level)) return;
        if (!mob.addTag(CALLED_TAG)) return; // already called (tags are saved with the mob)

        int count = 2 + mob.getRandom().nextInt(2);
        for (int i = 0; i < count; i++) {
            if (!(mob.getType().create(level, EntitySpawnReason.REINFORCEMENT) instanceof Mob minion)) continue;

            double angle = mob.getRandom().nextDouble() * Math.PI * 2;
            minion.snapTo(mob.getX() + Math.cos(angle) * 2, mob.getY(), mob.getZ() + Math.sin(angle) * 2, mob.getRandom().nextFloat() * 360.0F, 0.0F);
            if (!level.noCollision(minion)) {
                minion.snapTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), 0.0F);
            }
            minion.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(minion.position())), EntitySpawnReason.REINFORCEMENT, null);
            ((IChampions) minion).champions$setChampionTier(IChampions.NEVER_CHAMPION);
            minion.setTarget(player);
            level.addFreshEntity(minion);
        }
        level.playSound(null, mob.blockPosition(), SoundEvents.ZOMBIE_AMBIENT, SoundSource.HOSTILE, 2.0F, 0.6F);
    }
}
