package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * BigAffix (any champion)
 * Bigger (scale attribute: model and hitbox), tankier and harder-hitting, but slower
 */
public class BigAffix extends Affix {

    public BigAffix() {
        super("big");
    }

    @Override
    public void onApply(Mob mob) {
        ChampionsConfigAffixes config = ChampionsConfigAffixes.get();
        MobSpecificAffix.addBase(mob, Attributes.MAX_HEALTH, config.bigBonusHealth);
        MobSpecificAffix.addBase(mob, Attributes.ATTACK_DAMAGE, config.bigBonusDamage);
        MobSpecificAffix.multiplyBase(mob, Attributes.MOVEMENT_SPEED, config.bigSlowness);
        MobSpecificAffix.multiplyBase(mob, Attributes.SCALE, config.bigSize);
        // Apply the new hitbox now rather than on the mob's next tick
        mob.refreshDimensions();
    }
}
