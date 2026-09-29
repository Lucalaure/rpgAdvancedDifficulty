package crystal.champions.affix;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * ThiefAffix (endermen)
 * Its hits can knock the item out of your hand.
 */
public class ThiefAffix extends MobSpecificAffix {
    public static final float CHANCE = 0.25F;

    public ThiefAffix() {
        super("thief", "endermen", EntityTypes.ENDERMAN, mob -> mob instanceof Enderman);
    }

    @Override
    public void onHurt(LivingEntity champion, LivingEntity target) {
        if (!(target instanceof Player player) || !(player.level() instanceof ServerLevel level)) return;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty() || champion.getRandom().nextFloat() >= CHANCE) return;

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        ItemEntity item = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), held.copy());
        item.setPickUpDelay(40);
        Vec3 away = player.position().subtract(champion.position()).multiply(1, 0, 1).normalize().scale(0.3);
        item.setDeltaMovement(away.x, 0.3, away.z);
        level.addFreshEntity(item);
        level.playSound(null, player.blockPosition(), SoundEvents.ITEM_BREAK.value(), SoundSource.PLAYERS, 0.8F, 1.4F);
    }
}
