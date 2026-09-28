package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import crystal.champions.affix.Affix;
import crystal.champions.affix.AffixRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class BlindedAffix {

    @Inject(method = "tryAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void onChampionsAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof LivingEntity lTarget && this instanceof IChampions champion
                && champion.champions$getAffixesString().contains("blinded")) {

        Affix affix = AffixRegistry.ALL_AFFIXES.get("blinded");

        if (affix != null && lTarget.getRecentDamageSource() != null) {
                affix.onHurt((LivingEntity) (Object) this, lTarget);
            }
        }
    }
}
