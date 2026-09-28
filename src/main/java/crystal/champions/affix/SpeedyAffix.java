package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * SpeedyAffix (zombies only)
 * Much faster, but more fragile
 */
public class SpeedyAffix extends ZombieVariantAffix {

    public SpeedyAffix() {
        super("speedy");
    }

    @Override
    public void onApply(Mob mob) {
        ChampionsConfigAffixes config = ChampionsConfigAffixes.get();
        addBase(mob, Attributes.MAX_HEALTH, -config.speedyZombieHealthMalus);
        multiplyBase(mob, Attributes.MOVEMENT_SPEED, config.speedyZombieSpeed);
    }
}
