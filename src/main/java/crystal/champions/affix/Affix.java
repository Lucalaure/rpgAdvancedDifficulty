package crystal.champions.affix;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

public class Affix {
    private final String name;
    private int slots = 1;
    public Affix(String name) {this.name = name;}

    public String getName() { return name; }

    /** Affix slots this affix takes up on a champion (set from the config when registering). Stronger affixes cost more. */
    public int getSlots() { return slots; }
    public void setSlots(int slots) { this.slots = Math.max(1, slots); }

    /** Which mobs can roll this affix. Override to make a mob-specific variant (see MobSpecificAffix). */
    public boolean canApplyTo(Mob mob) { return true; }

    /** Translation key describing which mobs can have this affix (shown in the bestiary). */
    public String getMobsKey() { return "champions.bestiary.mobs.any"; }

    /** Mob spawned by "/champion demo <affix>". */
    public EntityType<? extends Mob> getExampleMob() { return EntityTypes.ZOMBIE; }

    /** Affixes sharing a group can't roll together on one champion. */
    public @Nullable String getExclusiveGroup() { return null; }

    /** Called once when the champion is created, after tier stats. Use for permanent stat changes. */
    public void onApply(Mob mob) { /* Once on creation */ }

    public void onTick(LivingEntity entity) { /* On all ticks */ }
    public void onAttack(LivingEntity champion, Mob target) {/* Every tick on mobs (target via getTarget) */}

    /** The champion landed a melee hit (or slime contact hit) on the target. */
    public void onHurt(LivingEntity champion, LivingEntity target) {/* When entity gets hurt (damage) */}

    /** The champion took damage (the hit landed). */
    public void onDamaged(LivingEntity champion, DamageSource source, float amount) { /* After being hurt */ }

    /** The champion died (after Undying and totems had their chance). */
    public void onDeath(LivingEntity champion, DamageSource source) { /* On death */ }

    /** A projectile owned by the champion is being added to the world (arrows, fireballs, potions...). */
    public void onProjectileSpawn(Mob owner, Projectile projectile) { /* Projectile fired */ }

    /** A projectile owned by the champion hit something. */
    public void onProjectileHit(Mob owner, Projectile projectile, HitResult hit) { /* Projectile impact */ }
}
