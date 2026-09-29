package crystal.champions.affix;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.cubemob.MagmaCube;
import net.minecraft.world.entity.monster.cubemob.Slime;

/**
 * StickyAffix (slimes and magma cubes)
 * Its hits slow you heavily for 3 seconds.
 */
public class StickyAffix extends MobSpecificAffix {
    public StickyAffix() {
        super("sticky", "slimes", EntityTypes.SLIME, mob -> mob instanceof Slime || mob instanceof MagmaCube);
    }

    @Override
    public void onHurt(LivingEntity champion, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 3));
    }
}
