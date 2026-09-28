package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
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
        if (entity.age % 10 != 0) return;
        World world = entity.getWorld();
        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, entity.getX(), entity.getY(), entity.getZ());
        cloud.setRadius(3.0f);
        cloud.setWaitTime(0);
        cloud.setDuration(10);
        StatusEffectInstance plagued = new StatusEffectInstance(StatusEffects.POISON, config.poisonDuration, config.poisonAmplifier);
        cloud.addEffect(plagued);
        world.spawnEntity(cloud);
        entity.removeStatusEffect(StatusEffects.POISON);
    }
}
