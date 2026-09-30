package crystal.champions.affix;

/**
 * VampiricAffix (any champion)
 * Heals for 50% of the damage it actually deals, melee or projectiles. Logic in AffixLifecycleMixin.
 */
public class VampiricAffix extends Affix {
    public static final float LIFESTEAL = 0.5F;

    public VampiricAffix() {
        super("vampiric");
    }
}
