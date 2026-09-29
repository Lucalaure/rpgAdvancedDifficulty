package crystal.champions.affix;

import crystal.champions.util.TemporaryBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * WebslingerAffix (spiders)
 * Throws cobwebs that trap you where they land. The webs disappear after a few seconds.
 */
public class WebslingerAffix extends MobSpecificAffix {
    private static final String WEB_TAG = "champions.web";
    private static final int COOLDOWN = 80;
    public static final int WEB_TICKS = 100;

    public WebslingerAffix() {
        super("webslinger", "spiders", EntityTypes.SPIDER, mob -> mob instanceof Spider);
    }

    @Override
    public void onAttack(LivingEntity entity, Mob mob) {
        LivingEntity target = mob.getTarget();
        if (mob.tickCount % COOLDOWN != 0 || target == null || !target.isAlive()) return;
        if (!(mob.level() instanceof ServerLevel level)) return;
        double distance = mob.distanceTo(target);
        if (distance < 3 || distance > 16 || !mob.hasLineOfSight(target)) return;

        ItemStack web = new ItemStack(Items.COBWEB);
        Snowball projectile = new Snowball(level, mob, web);
        projectile.addTag(WEB_TAG);
        double xd = target.getX() - mob.getX();
        double zd = target.getZ() - mob.getZ();
        double yd = target.getY(0.33) - projectile.getY();
        Projectile.spawnProjectileUsingShoot(projectile, level, web, xd, yd + Math.sqrt(xd * xd + zd * zd) * 0.2, zd, 1.2F, 2.0F);
        mob.playSound(SoundEvents.SPIDER_AMBIENT, 1.0F, 1.5F);
    }

    @Override
    public void onProjectileHit(Mob owner, Projectile projectile, HitResult hit) {
        if (!projectile.entityTags().contains(WEB_TAG) || !(projectile.level() instanceof ServerLevel level)) return;

        BlockPos pos = null;
        if (hit instanceof EntityHitResult entityHit) {
            pos = entityHit.getEntity().blockPosition();
        } else if (hit instanceof BlockHitResult blockHit) {
            pos = blockHit.getBlockPos().relative(blockHit.getDirection());
        }
        if (pos != null) {
            TemporaryBlocks.place(level, pos, Blocks.COBWEB.defaultBlockState(), WEB_TICKS);
        }
    }
}
