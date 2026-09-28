package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

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
        final float yaw = champion.getYaw();
        final double x = -Math.sin(yaw * 0.017453292F);
        final double z = Math.cos(yaw * 0.017453292F);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
        target.teleport(target.getX(), target.getY() + 0.1, target.getZ(), true);
        target.addVelocity(config.knockback * x, config.knockback * 0.3, config.knockback * z);
        target.velocityModified = true;
    }
}
