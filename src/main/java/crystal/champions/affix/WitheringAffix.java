package crystal.champions.affix;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * WitheringAffix (any champion)
 * Its hits and projectiles apply Wither II for 4 seconds.
 */
public class WitheringAffix extends Affix {
    public WitheringAffix() {
        super("withering");
    }

    @Override
    public void onHurt(LivingEntity champion, LivingEntity target) {
        wither(target);
    }

    @Override
    public void onProjectileHit(Mob owner, Projectile projectile, HitResult hit) {
        if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity target && target != owner) {
            wither(target);
        }
    }

    private static void wither(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 1));
    }
}
