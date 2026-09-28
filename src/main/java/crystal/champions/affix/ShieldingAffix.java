package crystal.champions.affix;

import crystal.champions.IChampions;
import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

/**
 * ShieldingAffix
 * В миксине отменяем урон раз в некоторое кол-во времени
 */
public class ShieldingAffix extends Affix {

    public ShieldingAffix() {
        super("shielding");
    }

    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Override
    public void onTick(LivingEntity entity) {
        if (entity.level().isClientSide()) return;

        final long time = entity.level().getGameTime();
        final boolean shieldWork = (time % config.shieldAllTime) < config.shieldWork;

        IChampions champion = (IChampions) entity;

        if (champion.champions$isShielding() != shieldWork) {
            champion.champions$setShielding(shieldWork);
        }
        if (shieldWork) {
            ((ServerLevel) entity.level()).sendParticles(
                    SpellParticleOption.create(ParticleTypes.EFFECT, -1, 1.0F),
                    entity.getX(), entity.getRandomY(), entity.getZ(),
                    0, 1.0, 1.0, 1.0, 0.8
            );
        }
    }
}
