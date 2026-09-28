package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(LivingEntity.class)
public class ReflectionMixin {
    /**
     * Наносим урон при уроне моба (шипы)
     *                 attacker.damage(source, 2.0f);
     *                 attacker.playSound(SoundEvents.ENCHANT_THORNS_HIT, 2, 1);
     */

    @Unique Random rnd = new Random();
    @Unique ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Inject(method = "damage", at = @At("TAIL"))
    private void applyReflection(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IChampions champion && champion.champions$getAffixesString().contains("reflection")) {

            if (!(source.getAttacker() instanceof LivingEntity attacker) || source.getAttacker() == null) return;

            attacker.damage(attacker.getWorld().getDamageSources().thorns(source.getSource()), config.reflectionDamage);
            final double random = rnd.nextDouble(-0.15, 0.15);
            final double x = attacker.getVelocity().x + random;
            final double z = attacker.getVelocity().z + random;
            attacker.takeKnockback(0.4, x, z);
            attacker.getWorld().playSound(
                    null,
                    attacker.getBlockPos(),
                    SoundEvents.ENCHANT_THORNS_HIT,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f
            );
        }
    }
}
