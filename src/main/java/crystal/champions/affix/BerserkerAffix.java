package crystal.champions.affix;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.illager.Vindicator;

/**
 * BerserkerAffix (vindicators)
 * Attacks faster the lower its health: up to about 3x as often near death.
 * Logic in BerserkerMixin.
 */
public class BerserkerAffix extends MobSpecificAffix {
    public BerserkerAffix() {
        super("berserker", "vindicators", EntityTypes.VINDICATOR, mob -> mob instanceof Vindicator);
    }

    /** Attack cooldown multiplier: 1.0 at full health down to 0.3 near death. */
    public static float cooldownMultiplier(float health, float maxHealth) {
        float fraction = maxHealth > 0 ? Math.max(0.0F, Math.min(1.0F, health / maxHealth)) : 1.0F;
        return 0.3F + 0.7F * fraction;
    }
}
