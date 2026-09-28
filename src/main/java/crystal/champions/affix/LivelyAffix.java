package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

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
    public void onAttack(LivingEntity entity, MobEntity mob) {
        if (entity.age % config.entityHealTime != 0) return;
        LivingEntity target = mob.getTarget();
        if (target == null) {
            mob.heal(config.entityHealNoTarget);
        } else {
            mob.heal(config.entityHeal);
        }
    }
}
