package crystal.champions.affix;

import crystal.champions.IBullet;
import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ShulkerBullet;

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
    public void onAttack(LivingEntity entity, Mob mob) {
        if (entity.tickCount % config.cooldownBeforeBulletMolten == 0) {
            LivingEntity target = mob.getTarget();
            if (target != null) {
                ShulkerBullet bullet = new ShulkerBullet(entity.level(), entity, target, Direction.Axis.Y);
                bullet.setPos(entity.getX(), entity.getEyeY() + 0.5, entity.getZ());

                // Working
                if (bullet instanceof IBullet i) {
                    i.champions$setMolten(true);
                }

                entity.level().addFreshEntity(bullet);
            }
        }
        if (entity.tickCount % 20 == 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false, false));
        }
    }
}
