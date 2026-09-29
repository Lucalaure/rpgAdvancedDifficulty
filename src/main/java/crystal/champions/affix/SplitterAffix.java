package crystal.champions.affix;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.cubemob.MagmaCube;
import net.minecraft.world.entity.monster.cubemob.Slime;

/**
 * SplitterAffix (slimes and magma cubes)
 * Splits into 2 extra pieces, and each piece becomes a champion with one of its affixes.
 * Logic in SplitterMixin.
 */
public class SplitterAffix extends MobSpecificAffix {
    public static final int EXTRA_PIECES = 2;

    public SplitterAffix() {
        super("splitter", "slimes", EntityTypes.SLIME, mob -> mob instanceof Slime || mob instanceof MagmaCube);
    }
}
