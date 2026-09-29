package crystal.champions.affix;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * AlchemistAffix (witches)
 * Also throws random nasty potions: Weakness, Mining Fatigue or Levitation.
 */
public class AlchemistAffix extends MobSpecificAffix {
    private static final int COOLDOWN = 100;
    private static final List<Supplier<MobEffectInstance>> EFFECTS = List.of(
            () -> new MobEffectInstance(MobEffects.WEAKNESS, 200, 1),
            () -> new MobEffectInstance(MobEffects.MINING_FATIGUE, 200, 1),
            () -> new MobEffectInstance(MobEffects.LEVITATION, 60, 0));

    public AlchemistAffix() {
        super("alchemist", "witches", EntityTypes.WITCH, mob -> mob instanceof Witch);
    }

    @Override
    public void onAttack(LivingEntity entity, Mob mob) {
        LivingEntity target = mob.getTarget();
        if (mob.tickCount % COOLDOWN != 0 || target == null || !target.isAlive()) return;
        if (!(mob.level() instanceof ServerLevel level) || mob.distanceTo(target) > 12 || !mob.hasLineOfSight(target)) return;

        MobEffectInstance effect = EFFECTS.get(mob.getRandom().nextInt(EFFECTS.size())).get();
        ItemStack potion = new ItemStack(Items.SPLASH_POTION);
        potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), List.of(effect), Optional.empty()));

        double xd = target.getX() - mob.getX();
        double zd = target.getZ() - mob.getZ();
        double yd = target.getEyeY() - 1.1 - mob.getY();
        ThrownSplashPotion thrown = new ThrownSplashPotion(level, mob, potion);
        Projectile.spawnProjectileUsingShoot(thrown, level, potion, xd, yd + Math.sqrt(xd * xd + zd * zd) * 0.2, zd, 0.75F, 8.0F);
    }
}
