package crystal.champions.affix;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.illager.AbstractIllager;

import java.util.List;

/**
 * WarlordAffix (illagers)
 * Arrives with 2-3 extra illagers, and other illagers within 16 blocks have Strength while it's alive.
 */
public class WarlordAffix extends MobSpecificAffix {
    private static final double RADIUS = 16.0;

    public WarlordAffix() {
        super("warlord", "illagers", EntityTypes.PILLAGER, mob -> mob instanceof AbstractIllager);
    }

    private static final String SUMMONED_TAG = "champions.warlord_summoned";
    private static final List<EntityType<? extends Mob>> FOLLOWERS = List.of(EntityTypes.PILLAGER, EntityTypes.VINDICATOR);

    @Override
    public void onTick(LivingEntity entity) {
        // First tick in the world: bring 2-3 followers (the tag is saved, so only once)
        if (entity instanceof Mob warlord && warlord.addTag(SUMMONED_TAG)) {
            spawnMinions(warlord, FOLLOWERS, 2 + warlord.getRandom().nextInt(2), warlord.getTarget());
        }
        if (entity.tickCount % 20 != 0 || !(entity.level() instanceof ServerLevel level)) return;
        for (AbstractIllager ally : level.getEntitiesOfClass(AbstractIllager.class, entity.getBoundingBox().inflate(RADIUS),
                illager -> illager != entity && illager.isAlive())) {
            ally.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 40, 0, true, true));
        }
    }
}
