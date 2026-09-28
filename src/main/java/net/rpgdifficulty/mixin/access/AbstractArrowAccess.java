package net.rpgdifficulty.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

// AbstractArrow has no public base damage getter anymore (1.21.1 had getDamage())
@Mixin(AbstractArrow.class)
public interface AbstractArrowAccess {

    @Accessor("baseDamage")
    double rpgdifficulty$getBaseDamage();
}
