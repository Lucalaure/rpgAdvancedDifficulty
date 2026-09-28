package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.rpgdifficulty.access.ZombieEntityAccess;

/**
 * BigAffix (zombies only)
 * Bigger, tankier and harder-hitting, but slower
 */
public class BigAffix extends ZombieVariantAffix {

    public BigAffix() {
        super("big");
    }

    @Override
    public void onApply(Mob mob) {
        ChampionsConfigAffixes config = ChampionsConfigAffixes.get();
        addBase(mob, Attributes.MAX_HEALTH, config.bigZombieBonusHealth);
        addBase(mob, Attributes.ATTACK_DAMAGE, config.bigZombieBonusDamage);
        multiplyBase(mob, Attributes.MOVEMENT_SPEED, config.bigZombieSlowness);
        ((ZombieEntityAccess) mob).setBig();
    }
}
