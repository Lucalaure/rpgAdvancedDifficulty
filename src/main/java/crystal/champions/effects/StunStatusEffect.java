package crystal.champions.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StunStatusEffect extends StatusEffect {
    private static final Map<UUID, Float> frozenYaw = new ConcurrentHashMap<>();
    private static final Map<UUID, Float> frozenPitch = new ConcurrentHashMap<>();

    public StunStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x999999);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.setVelocity(0, 0, 0);

        if (entity instanceof PlayerEntity player) {
            UUID id = player.getUuid();

            frozenYaw.putIfAbsent(id, player.getYaw());
            frozenPitch.putIfAbsent(id, player.getPitch());
            final float baseYaw = frozenYaw.get(id);
            final float basePitch = frozenPitch.get(id);

            player.setYaw(baseYaw + (float)((Math.random() - 0.5) * 0.1));
            player.setPitch(basePitch + (float)((Math.random() - 0.5) * 0.1));

            player.setSprinting(false);
            player.setJumping(false);
            player.velocityDirty = true;
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
