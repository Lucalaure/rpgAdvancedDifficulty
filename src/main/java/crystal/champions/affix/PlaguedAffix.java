package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
/**
 * PlaguedAffix
 * Здесь мы на моба ставим туманное зелье на отравление и все
 */
public class PlaguedAffix extends Affix {

    public PlaguedAffix() {
        super("plagued");
    }

    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Override
    public void onTick(LivingEntity entity) {
        if (entity.tickCount % 10 != 0) return;
        Level world = entity.level();
        AreaEffectCloud cloud = new AreaEffectCloud(world, entity.getX(), entity.getY(), entity.getZ());
        cloud.setRadius(3.0f);
        cloud.setWaitTime(0);
        cloud.setDuration(10);
        MobEffectInstance plagued = new MobEffectInstance(MobEffects.POISON, config.poisonDuration, config.poisonAmplifier);
        cloud.addEffect(plagued);
        world.addFreshEntity(cloud);
        entity.removeEffect(MobEffects.POISON);
    }
}
