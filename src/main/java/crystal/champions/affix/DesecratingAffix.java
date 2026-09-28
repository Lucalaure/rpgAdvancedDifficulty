package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

/**
 * DesecratingAffix
 * Создаем туманное зелие на blockpos target
 */
public class DesecratingAffix extends Affix {

    public DesecratingAffix() {
        super("desecrating");
    }
    
    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Override
    public void onAttack(LivingEntity entity, Mob mob) {
        if (entity.tickCount % config.timeBeforeDesecrating != 0) return;
        LivingEntity target = mob.getTarget();
        if (target == null) return;
        Level world = entity.level();
        AreaEffectCloud cloud = new AreaEffectCloud(world, target.getX(), target.getY(), target.getZ());
        cloud.setRadius(3.0f);
        cloud.setWaitTime(10);
        cloud.setDuration(config.cloudDuration);
        MobEffectInstance desecrating = new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 10, 1);
        cloud.addEffect(desecrating);

        world.addFreshEntity(cloud);
    }
}
