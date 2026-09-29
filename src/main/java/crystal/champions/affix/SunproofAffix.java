package crystal.champions.affix;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;

/**
 * SunproofAffix (zombies and skeletons)
 * Doesn't burn in daylight. Logic in SunproofMixin.
 */
public class SunproofAffix extends MobSpecificAffix {
    public SunproofAffix() {
        super("sunproof", "zombies_skeletons", EntityTypes.ZOMBIE, mob -> mob instanceof Zombie || mob instanceof AbstractSkeleton);
    }
}
