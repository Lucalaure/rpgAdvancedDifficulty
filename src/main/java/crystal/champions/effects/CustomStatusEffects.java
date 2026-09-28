package crystal.champions.effects;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public class CustomStatusEffects {
    private CustomStatusEffects() {
        /* This utility class should not be instantiated */
    }

    public static final Holder<MobEffect> STUN = reg(new StunStatusEffect());

    private static Holder<MobEffect> reg(MobEffect statusEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath("champions", "stun"), statusEffect);
    }
    public static void registerEffects() {
        // Register
    }
}
