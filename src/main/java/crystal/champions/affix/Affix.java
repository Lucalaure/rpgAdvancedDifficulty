package crystal.champions.affix;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class Affix {
    private final String name;
    public Affix(String name) {this.name = name;}

    public String getName() { return name; }

    public void onTick(LivingEntity entity) { /* On all ticks */ }
    public void onAttack(LivingEntity champion, Mob target) {/* When mob attacks (goal) */}

    public void onHurt(LivingEntity champion, LivingEntity target) {/* When entity gets hurt (damage) */}
}