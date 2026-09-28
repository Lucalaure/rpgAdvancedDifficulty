package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import crystal.champions.affix.Affix;
import crystal.champions.affix.AffixRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Mob.class)
public class ParalyzingMixin {
    @Inject(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void onChampionsAttack(ServerLevel level, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof LivingEntity lTarget && this instanceof IChampions champion
                && champion.champions$getAffixesString().contains("paralyzing")) {

            Affix affix = AffixRegistry.ALL_AFFIXES.get("paralyzing");

            if (affix != null && lTarget.getLastDamageSource() != null) {
                    affix.onHurt((LivingEntity) (Object) this, lTarget);
            }
        }
    }
}
