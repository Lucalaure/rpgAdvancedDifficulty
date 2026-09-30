package crystal.champions.affix;

import crystal.champions.util.DelayedExplosions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.rpgdifficulty.RpgDifficultyMain;

/**
 * VolatileAffix (any champion)
 * Explodes 1.5 seconds after dying (with a hissing, smoking warning). The explosion doesn't break blocks.
 */
public class VolatileAffix extends Affix {
    public static final int FUSE_TICKS = 30;
    private static final float POWER = 3.0F;

    public VolatileAffix() {
        super("volatile");
    }

    @Override
    public void onDeath(LivingEntity champion, DamageSource source) {
        if (champion.level() instanceof ServerLevel level) {
            float power = Math.min(POWER, RpgDifficultyMain.CONFIG.maxCreeperExplosionPower);
            DelayedExplosions.schedule(level, champion.position().add(0, champion.getBbHeight() * 0.5, 0), power, FUSE_TICKS);
        }
    }
}
