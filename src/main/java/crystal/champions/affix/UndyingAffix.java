package crystal.champions.affix;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * UndyingAffix (any champion)
 * The first time it would die, it comes back with half its health, like a totem of undying,
 * and is invulnerable for 2 seconds. Once per champion (saved with it). Logic in AffixLifecycleMixin.
 */
public class UndyingAffix extends Affix {
    public static final String USED_TAG = "champions.undying_used";

    public UndyingAffix() {
        super("undying");
    }

    /** Returns true if the champion was revived. */
    public static boolean tryRevive(LivingEntity champion) {
        if (!champion.addTag(USED_TAG)) return false; // already used its second life
        champion.setHealth(champion.getMaxHealth() * 0.5F);
        champion.removeAllEffects();
        champion.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 40, 4));
        champion.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
        // Totem of undying particles and sound
        champion.level().broadcastEntityEvent(champion, (byte) 35);
        return true;
    }
}
