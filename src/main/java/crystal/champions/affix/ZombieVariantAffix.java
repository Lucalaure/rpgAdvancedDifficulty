package crystal.champions.affix;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.jspecify.annotations.Nullable;

/**
 * Base for zombie-only variant affixes (zombies, husks, drowned, zombie villagers, zombified piglins).
 * Variants in the same group change the zombie's build, so only one of them can roll.
 */
public abstract class ZombieVariantAffix extends Affix {

    protected ZombieVariantAffix(String name) {
        super(name);
    }

    @Override
    public boolean canApplyTo(Mob mob) {
        return mob instanceof Zombie && !mob.isBaby();
    }

    @Override
    public String getMobsKey() {
        return "champions.bestiary.mobs.zombies";
    }

    @Override
    public @Nullable String getExclusiveGroup() {
        return "zombie_build";
    }

    protected static void addBase(Mob mob, Holder<Attribute> attribute, double amount) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(Math.max(1.0, instance.getBaseValue() + amount));
        }
    }

    protected static void multiplyBase(Mob mob, Holder<Attribute> attribute, double factor) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() * factor);
        }
    }
}
