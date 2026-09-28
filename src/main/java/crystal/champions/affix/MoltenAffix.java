package crystal.champions.affix;

import crystal.champions.IBullet;
import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.math.Direction;

/**
 * Создаем bullet, и суем в него нбт
 * Я объединил molten и enkindling
 * ShulkerBulletEntity
 */
public class MoltenAffix extends Affix {

    public MoltenAffix() {
        super("molten");
    }

    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Override
    public void onAttack(LivingEntity entity, MobEntity mob) {
        if (entity.age % config.cooldownBeforeBulletMolten == 0) {
            LivingEntity target = mob.getTarget();
            if (target != null) {
                ShulkerBulletEntity bullet = new ShulkerBulletEntity(entity.getWorld(), entity, target, Direction.Axis.Y);
                bullet.setPosition(entity.getX(), entity.getEyeY() + 0.5, entity.getZ());

                // Working
                if (bullet instanceof IBullet i) {
                    i.champions$setMolten(true);
                }

                entity.getWorld().spawnEntity(bullet);
            }
        } else if (entity.age % 20 == 0) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 40, 0, false, false, false));
        }
    }
}
