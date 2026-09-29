package crystal.champions.affix;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.illager.AbstractIllager;

/**
 * WarlordAffix (illagers)
 * Other illagers within 16 blocks have Strength while it's alive.
 */
public class WarlordAffix extends MobSpecificAffix {
    private static final double RADIUS = 16.0;

    public WarlordAffix() {
        super("warlord", "illagers", EntityTypes.PILLAGER, mob -> mob instanceof AbstractIllager);
    }

    @Override
    public void onTick(LivingEntity entity) {
        if (entity.tickCount % 20 != 0 || !(entity.level() instanceof ServerLevel level)) return;
        for (AbstractIllager ally : level.getEntitiesOfClass(AbstractIllager.class, entity.getBoundingBox().inflate(RADIUS),
                illager -> illager != entity && illager.isAlive())) {
            ally.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 40, 0, true, true));
        }
    }
}
