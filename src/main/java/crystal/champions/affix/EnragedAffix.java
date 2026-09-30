package crystal.champions.affix;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * EnragedAffix (any champion)
 * Below 40% health it gets Strength and Speed and gives off red particles.
 */
public class EnragedAffix extends Affix {
    public static final float THRESHOLD = 0.4F;
    private static final DustParticleOptions RED = new DustParticleOptions(0xFF2020, 1.5F);

    public EnragedAffix() {
        super("enraged");
    }

    @Override
    public void onTick(LivingEntity entity) {
        if (entity.tickCount % 10 != 0 || entity.getHealth() >= entity.getMaxHealth() * THRESHOLD) return;
        entity.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 30, 0, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 30, 0, false, false, false));
        if (entity.level() instanceof ServerLevel level) {
            level.sendParticles(RED, entity.getX(), entity.getY(0.6), entity.getZ(), 6, entity.getBbWidth() * 0.4, entity.getBbHeight() * 0.3, entity.getBbWidth() * 0.4, 0.0);
        }
    }
}
