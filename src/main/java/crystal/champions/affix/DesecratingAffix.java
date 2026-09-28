package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

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
    public void onAttack(LivingEntity entity, MobEntity mob) {
        if (entity.age % config.timeBeforeDesecrating != 0) return;
        LivingEntity target = mob.getTarget();
        if (target == null) return;
        World world = entity.getWorld();
        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, target.getX(), target.getY(), target.getZ());
        cloud.setRadius(3.0f);
        cloud.setWaitTime(10);
        cloud.setDuration(config.cloudDuration);
        StatusEffectInstance desecrating = new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 10, 1);
        cloud.addEffect(desecrating);

        world.spawnEntity(cloud);
    }
}
