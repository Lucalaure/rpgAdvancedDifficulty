package crystal.champions.affix;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;

/**
 * SunproofAffix (zombies and skeletons)
 * Doesn't burn in daylight. Logic in SunproofMixin.
 */
public class SunproofAffix extends MobSpecificAffix {
    public SunproofAffix() {
        super("sunproof", "zombies_skeletons", EntityType.ZOMBIE, mob -> mob instanceof Zombie || mob instanceof AbstractSkeleton);
    }
}
