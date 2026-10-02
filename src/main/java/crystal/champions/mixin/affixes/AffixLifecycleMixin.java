package crystal.champions.mixin.affixes;

import crystal.champions.ChampionAdvancements;
import crystal.champions.IChampions;
import crystal.champions.affix.AffixEvents;
import crystal.champions.affix.UndyingAffix;
import crystal.champions.affix.VampiricAffix;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Undying (revive instead of dying), Volatile (death hook), Vampiric (heal the attacker)
 * and Stormcaller's immunity to its own lightning.
 */
@Mixin(LivingEntity.class)
public class AffixLifecycleMixin {
    @Shadow protected boolean dead;
    @Unique private float champions$healthBeforeHit;

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void champions$beforeHurt(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (source.is(DamageTypes.LIGHTNING_BOLT) && self instanceof IChampions champion && champion.champions$hasAffix("stormcaller")) {
            cir.setReturnValue(false);
            return;
        }
        this.champions$healthBeforeHit = self.getHealth();
    }

    // Vampiric: the attacker heals for part of the damage this entity actually took
    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void champions$afterHurt(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!cir.getReturnValueZ() || !(source.getEntity() instanceof LivingEntity attacker) || attacker == self) return;
        if (!(attacker instanceof IChampions champion) || !champion.champions$hasAffix("vampiric") || !attacker.isAlive()) return;

        float dealt = this.champions$healthBeforeHit - self.getHealth();
        if (dealt > 0) {
            attacker.heal(dealt * VampiricAffix.LIFESTEAL);
            level.sendParticles(ParticleTypes.HEART, attacker.getX(), attacker.getY(1.0) + 0.3, attacker.getZ(), 1, 0.2, 0.1, 0.2, 0.0);
        }
    }

    // Undying: a second life when nothing else (like a totem) saved it; /kill and the void still work
    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
    private void champions$undying(DamageSource killingDamage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!cir.getReturnValueZ() && !killingDamage.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && self instanceof IChampions champion && champion.champions$hasAffix("undying")
                && UndyingAffix.tryRevive(self)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void champions$onDeath(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        // die() does nothing for entities that are already dead, so only fire once
        if (!self.isRemoved() && !this.dead) {
            AffixEvents.onDeath(self, source);
            ChampionAdvancements.onChampionKilled(self, source);
        }
    }
}
