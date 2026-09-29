package crystal.champions.affix;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

/**
 * StalkerAffix (creepers)
 * Invisible (its effect particles still give it away) until it starts to hiss.
 */
public class StalkerAffix extends MobSpecificAffix {
    public StalkerAffix() {
        super("stalker", "creepers", EntityTypes.CREEPER, mob -> mob instanceof Creeper);
    }

    @Override
    public void onTick(LivingEntity entity) {
        if (!(entity instanceof Creeper creeper)) return;
        if (creeper.getSwellDir() > 0 || creeper.isIgnited()) {
            creeper.removeEffect(MobEffects.INVISIBILITY);
        } else if (creeper.tickCount % 10 == 0) {
            creeper.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 30, 0, false, true, false));
        }
    }
}
