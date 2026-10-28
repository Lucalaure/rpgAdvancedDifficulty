package crystal.champions.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;


public class StunStatusEffect extends MobEffect {

    public StunStatusEffect() {
        super(MobEffectCategory.HARMFUL, 0x999999);
        // Synced modifiers keep the player frozen client-side too
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("champions", "effect.stun"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath("champions", "effect.stun"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
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
