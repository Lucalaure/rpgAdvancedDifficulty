package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/**
 * LivelyAffix
 * Просто реген моба
 */
public class LivelyAffix extends Affix {

    public LivelyAffix() {
        super("lively");
    }

    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Override
    public void onAttack(LivingEntity entity, Mob mob) {
        if (entity.tickCount % config.entityHealTime != 0) return;
        LivingEntity target = mob.getTarget();
        if (target == null) {
            mob.heal(config.entityHealNoTarget);
        } else {
            mob.heal(config.entityHeal);
        }
    }
}
