package crystal.champions.effects;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;


public class StunStatusEffect extends MobEffect {

    public StunStatusEffect() {
        super(MobEffectCategory.HARMFUL, 0x999999);
        // 26.3: applyEffectTick runs only on the server, so the client no longer zeroes the player's
        // velocity itself. These synced modifiers keep the player frozen client-side as before.
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, Identifier.fromNamespaceAndPath("champions", "effect.stun"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.JUMP_STRENGTH, Identifier.fromNamespaceAndPath("champions", "effect.stun"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        // Movement and jumping are locked by the attribute modifiers above; this stops any leftover momentum
        entity.setDeltaMovement(0, 0, 0);
        if (entity instanceof Player player) {
            player.setSprinting(false);
            player.setJumping(false);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
