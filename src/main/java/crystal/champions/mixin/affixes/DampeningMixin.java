package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static crystal.champions.config.ChampionsConfigAffixes.get;

@Mixin(LivingEntity.class)
public class DampeningMixin {
    /**
     * Уменьшаем урон
     * Из важного
     *                  if (!source.is(DamageTypeTags.IS_PROJECTILE) || !source.is(DamageTypeTags.IS_FIRE)) return amount * 0.5f;
     * Просто source сравниваем и уменьшаем в 2 раза урон
     */
    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
    private float applyDampening(float amount, ServerLevel level, DamageSource source) {
        // Indirect damage: projectiles, explosions, and anything not dealt directly by its causer (e.g. potions)
        final boolean indirect = source.is(DamageTypeTags.IS_PROJECTILE) || source.is(DamageTypeTags.IS_EXPLOSION) || !source.isDirect();
        if (this instanceof IChampions champion && champion.champions$getAffixesString().contains("dampening")
                && indirect) return amount * get().dampeningAmount;

        return amount;
    }
}
