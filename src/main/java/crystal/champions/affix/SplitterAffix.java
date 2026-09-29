package crystal.champions.affix;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.cubemob.MagmaCube;
import net.minecraft.world.entity.monster.cubemob.Slime;

/**
 * SplitterAffix (slimes and magma cubes)
 * Splits into 2 extra pieces. Each piece has a 50% chance to keep one of its other affixes
 * (never Splitter itself); pieces never drop champion loot. Logic in SplitterMixin.
 */
public class SplitterAffix extends MobSpecificAffix {
    public static final int EXTRA_PIECES = 2;
    /** Chance for each piece to keep one of the parent's other affixes. */
    public static final float INHERIT_CHANCE = 0.5F;

    public SplitterAffix() {
        super("splitter", "slimes", EntityTypes.SLIME, mob -> mob instanceof Slime || mob instanceof MagmaCube);
    }
}
