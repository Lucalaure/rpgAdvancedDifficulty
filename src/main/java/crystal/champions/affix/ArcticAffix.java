package crystal.champions.affix;

import crystal.champions.IBullet;
import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ShulkerBullet;

/**
 * Создаем bullet, и суем в него нбт
 * ShulkerBulletEntity
 */
public class ArcticAffix extends Affix {

    public ArcticAffix() {
        super("arctic");
    }

    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Override
    public void onAttack(LivingEntity entity, Mob mob) {
        if (entity.tickCount % config.cooldownBeforeBulletArtic != 0) return;
        LivingEntity target = mob.getTarget();

        if (target != null && target.isAlive()) {
            ShulkerBullet bullet = new ShulkerBullet(entity.level(), mob, target, Direction.Axis.Y);
            bullet.setPos(entity.getX(), entity.getEyeY() + 0.5, entity.getZ());

            // Working
            if (bullet instanceof IBullet i) {
                i.champions$setArctic(true);
            }

            mob.level().addFreshEntity(bullet);
        }
    }
}
