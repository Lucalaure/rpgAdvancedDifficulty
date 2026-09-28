package crystal.champions.affix;

import crystal.champions.IBullet;
import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.math.Direction;

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
    public void onAttack(LivingEntity entity, MobEntity mob) {
        if (entity.age % config.cooldownBeforeBulletArtic != 0) return;
        LivingEntity target = mob.getTarget();

        if (target != null && target.isAlive()) {
            ShulkerBulletEntity bullet = new ShulkerBulletEntity(entity.getWorld(), mob, target, Direction.Axis.Y);
            bullet.setPosition(entity.getX(), entity.getEyeY() + 0.5, entity.getZ());

            // Working
            if (bullet instanceof IBullet i) {
                i.champions$setArctic(true);
            }

            mob.getWorld().spawnEntity(bullet);
        }
    }
}
