package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
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
     *                 attacker.hurtServer(level, source, 2.0f);
     *                 attacker.playSound(SoundEvents.ENCHANT_THORNS_HIT, 2, 1);
     */

    @Unique Random rnd = new Random();
    @Unique ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Inject(method = "hurtServer", at = @At("TAIL"))
    private void applyReflection(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IChampions champion && champion.champions$getAffixesString().contains("reflection")) {

            if (!(source.getEntity() instanceof LivingEntity attacker) || source.getEntity() == null) return;

            DamageSource thorns = attacker.level().damageSources().thorns(source.getDirectEntity());
            attacker.hurtServer(level, thorns, config.reflectionDamage);
            final double random = rnd.nextDouble(-0.15, 0.15);
            final double x = attacker.getDeltaMovement().x + random;
            final double z = attacker.getDeltaMovement().z + random;
            attacker.knockback(0.4, x, z, thorns, config.reflectionDamage);
            attacker.level().playSound(
                    null,
                    attacker.blockPosition(),
                    SoundEvents.THORNS_HIT,
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
            );
        }
    }
}
