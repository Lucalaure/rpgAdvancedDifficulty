package crystal.champions.affix;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.phys.Vec3;

/**
 * BlinkAffix (endermen)
 * Teleports behind its attacker after being hit.
 */
public class BlinkAffix extends MobSpecificAffix {
    public BlinkAffix() {
        super("blink", "endermen", EntityTypes.ENDERMAN, mob -> mob instanceof Enderman);
    }

    @Override
    public void onDamaged(LivingEntity champion, DamageSource source, float amount) {
        if (!(source.getEntity() instanceof LivingEntity attacker) || attacker == champion || !champion.isAlive()) return;
        if (champion.distanceTo(attacker) > 16) return;

        Vec3 look = attacker.getLookAngle().multiply(1, 0, 1);
        if (look.lengthSqr() < 1.0E-4) return;
        Vec3 behind = attacker.position().subtract(look.normalize().scale(2.0));
        champion.randomTeleport(behind.x, attacker.getY(), behind.z, true, state -> false);
    }
}
