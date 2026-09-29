package crystal.champions.affix;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;

import java.util.List;

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
    @SuppressWarnings("unchecked")
    public void onAttack(LivingEntity entity, Mob mob) {
        if (!(mob.getTarget() instanceof Player player) || !(mob.level() instanceof ServerLevel level)) return;
        if (!mob.addTag(CALLED_TAG)) return; // already called (tags are saved with the mob)

        spawnMinions(mob, List.of((EntityType<? extends Mob>) mob.getType()), 2 + mob.getRandom().nextInt(2), player);
        level.playSound(null, mob.blockPosition(), SoundEvents.ZOMBIE_AMBIENT, SoundSource.HOSTILE, 2.0F, 0.6F);
    }
}
