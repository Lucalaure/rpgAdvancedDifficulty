package crystal.champions.affix;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.function.Predicate;

/**
 * Base for affixes limited to certain mobs. Subclasses pass which mobs can roll it,
 * the bestiary label key and the mob "/champion demo" spawns.
 */
public abstract class MobSpecificAffix extends Affix {
    private final Predicate<Mob> mobs;
    private final String mobsKey;
    private final EntityType<? extends Mob> exampleMob;

    protected MobSpecificAffix(String name, String mobsKey, EntityType<? extends Mob> exampleMob, Predicate<Mob> mobs) {
        super(name);
        this.mobs = mobs;
        this.mobsKey = "champions.bestiary.mobs." + mobsKey;
        this.exampleMob = exampleMob;
    }

    @Override
    public boolean canApplyTo(Mob mob) {
        return mobs.test(mob);
    }

    @Override
    public String getMobsKey() {
        return mobsKey;
    }

    @Override
    public EntityType<? extends Mob> getExampleMob() {
        return exampleMob;
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
