package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * HastyAffix
 * Накладываем эффект speed на чемпиона
 */
public class HastyAffix extends Affix {

    public HastyAffix() {
        super("hasty");
    }

    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Override
    public void onTick(LivingEntity entity) {
        if (entity.tickCount % 20 == 0) {
            entity.addEffect(new MobEffectInstance(
                    MobEffects.SPEED, 20, config.hastyAmplifier, true, false, false
            ));
        }
    }
}
