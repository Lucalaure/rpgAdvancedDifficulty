package crystal.champions.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class CustomStatusEffects {
    private CustomStatusEffects() {
        /* This utility class should not be instantiated */
    }

    public static final RegistryEntry<StatusEffect> STUN = reg(new StunStatusEffect());

    private static RegistryEntry<StatusEffect> reg(StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of("champions", "stun"), statusEffect);
    }
    public static void registerEffects() {
        // Register
    }
}
