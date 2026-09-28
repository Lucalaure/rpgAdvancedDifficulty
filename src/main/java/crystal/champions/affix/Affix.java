package crystal.champions.affix;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jspecify.annotations.Nullable;

public class Affix {
    private final String name;
    public Affix(String name) {this.name = name;}

    public String getName() { return name; }

    /** Which mobs can roll this affix. Override to make a mob-specific variant (e.g. zombies only). */
    public boolean canApplyTo(Mob mob) { return true; }

    /** Translation key describing which mobs can have this affix (shown in the bestiary). */
    public String getMobsKey() { return "champions.bestiary.mobs.any"; }

    /** Affixes sharing a group can't roll together on one champion (e.g. big vs speedy). */
    public @Nullable String getExclusiveGroup() { return null; }

    /** Called once when the champion is created, after tier stats. Use for permanent stat changes. */
    public void onApply(Mob mob) { /* Once on creation */ }

    public void onTick(LivingEntity entity) { /* On all ticks */ }
    public void onAttack(LivingEntity champion, Mob target) {/* When mob attacks (goal) */}

    public void onHurt(LivingEntity champion, LivingEntity target) {/* When entity gets hurt (damage) */}
}
