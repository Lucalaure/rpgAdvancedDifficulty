package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * KnockingAffix
 * Откидываем игрока
 */
public class KnockingAffix extends Affix {

    public KnockingAffix() {
        super("knocking");
    }

    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Override
    public void onHurt(LivingEntity champion, LivingEntity target) {
        final float yaw = champion.getYRot();
        final double x = -Math.sin(yaw * 0.017453292F);
        final double z = Math.cos(yaw * 0.017453292F);
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
        target.randomTeleport(target.getX(), target.getY() + 0.1, target.getZ(), true, state -> false);
        target.push(config.knockback * x, config.knockback * 0.3, config.knockback * z);
        target.syncVelocity = true;
    }
}
