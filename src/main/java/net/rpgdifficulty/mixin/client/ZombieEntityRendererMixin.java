package net.rpgdifficulty.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.entity.monster.zombie.Zombie;
import crystal.champions.config.ChampionsConfigServer;
import net.rpgdifficulty.access.ZombieEntityAccess;

@Environment(EnvType.CLIENT)
@Mixin(ZombieRenderer.class)
public abstract class ZombieEntityRendererMixin extends AbstractZombieRenderer<Zombie, ZombieRenderState, ZombieModel<ZombieRenderState>> {

    // Big zombie flag, copied from the entity into the render state during extraction
    @Unique
    private static final RenderStateDataKey<Boolean> RPGDIFFICULTY_BIG = RenderStateDataKey.create(() -> "rpgdifficulty:big_zombie");

    public ZombieEntityRendererMixin(Context ctx, ZombieModel<ZombieRenderState> bodyModel, ZombieModel<ZombieRenderState> babyModel,
            ArmorModelSet<ZombieModel<ZombieRenderState>> armorSet, ArmorModelSet<ZombieModel<ZombieRenderState>> babyArmorSet) {
        super(ctx, bodyModel, babyModel, armorSet, babyArmorSet);
    }

    @Override
    public void extractRenderState(Zombie entity, ZombieRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.setData(RPGDIFFICULTY_BIG, ((ZombieEntityAccess) entity).rpgdifficulty$isBig());
    }

    @Override
    protected void scale(ZombieRenderState state, PoseStack matrices) {
        if (Boolean.TRUE.equals(state.getData(RPGDIFFICULTY_BIG)))
            matrices.scale(ChampionsConfigServer.get().bigZombieSize, ChampionsConfigServer.get().bigZombieSize, ChampionsConfigServer.get().bigZombieSize);
        super.scale(state, matrices);
    }

}
