package crystal.champions.affix;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

public class Affix {
    private final String name;
    public Affix(String name) {this.name = name;}

    public String getName() { return name; }

    public void onTick(LivingEntity entity) { /* On all ticks */ }
    public void onAttack(LivingEntity champion, MobEntity target) {/* When mob attacks (goal) */}

    public void onHurt(LivingEntity champion, LivingEntity target) {/* When entity gets hurt (damage) */}
}