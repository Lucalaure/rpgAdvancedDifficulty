package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * MagneticAffix
 * Из интересно здесь именно через set надо target.setDeltaMovement(pX, pY, pZ);
 * Без него не будет как в оригинальном моде
 */
public class MagneticAffix extends Affix {
    public MagneticAffix() {
        super("magnetic");
    }

    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Override
    public void onAttack(LivingEntity entity, Mob mob) {
        if (entity.tickCount % config.magneticCooldown <= config.magneticPullTime) return;
        LivingEntity target = mob.getTarget();
        if (target != null) {
            Vec3 pullDir = entity.position().subtract(target.position()).normalize();
            final double i = 0.01 * config.strength;
            final float pX = (float) (pullDir.x * i + target.getDeltaMovement().x * 0.5);
            final float pY = (float) (pullDir.y * i + target.getDeltaMovement().y * 0.6);
            final float pZ = (float) (pullDir.z * i + target.getDeltaMovement().z * 0.5);
            target.setDeltaMovement(pX, pY, pZ);

            target.syncVelocity = true;
        }
    }
}