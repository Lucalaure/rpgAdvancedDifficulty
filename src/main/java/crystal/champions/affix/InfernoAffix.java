package crystal.champions.affix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.HitResult;

/**
 * InfernoAffix (blazes and ghasts)
 * Its fireballs leave a patch of fire where they land (only with mob griefing on).
 */
public class InfernoAffix extends MobSpecificAffix {
    public InfernoAffix() {
        super("inferno", "blazes_ghasts", EntityTypes.BLAZE, mob -> mob instanceof Blaze || mob instanceof Ghast);
    }

    @Override
    public void onProjectileHit(Mob owner, Projectile projectile, HitResult hit) {
        if (!(projectile instanceof AbstractHurtingProjectile) || !(projectile.level() instanceof ServerLevel level)) return;
        if (!level.getGameRules().get(GameRules.MOB_GRIEFING)) return;

        BlockPos center = BlockPos.containing(hit.getLocation());
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 1; dy >= -1; dy--) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (level.getBlockState(pos).isAir() && BaseFireBlock.canBePlacedAt(level, pos, Direction.UP)) {
                        level.setBlockAndUpdate(pos, BaseFireBlock.getState(level, pos));
                        break;
                    }
                }
            }
        }
    }
}
