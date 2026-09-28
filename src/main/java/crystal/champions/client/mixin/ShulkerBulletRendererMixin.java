package crystal.champions.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import crystal.champions.IBullet;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ShulkerBulletRenderer;
import net.minecraft.client.renderer.entity.state.ShulkerBulletRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBulletRenderer.class)
public class ShulkerBulletRendererMixin {

    @Unique private static final Identifier ARCTIC_TEXTURE = Identifier.fromNamespaceAndPath("champions", "textures/entity/arctic.png");
    @Unique private static final Identifier MOLTEN_TEXTURE = Identifier.fromNamespaceAndPath("champions", "textures/entity/molten.png");
    @Unique private static final Identifier DEFAULT_TEXTURE = Identifier.withDefaultNamespace("textures/entity/shulker/spark.png");

    /**
     * Texture override, extracted from IBullet into the render state (null = vanilla)
     */
    @Unique private static final RenderStateDataKey<Identifier> CHAMPIONS_TEXTURE = RenderStateDataKey.create(() -> "champions:bullet_texture");

    @Unique private static final String SUBMIT =
            "submit(Lnet/minecraft/client/renderer/entity/state/ShulkerBulletRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V";

    /**
     * Check IBullet while we still have the entity
     */
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/projectile/ShulkerBullet;Lnet/minecraft/client/renderer/entity/state/ShulkerBulletRenderState;F)V",
            at = @At("TAIL")
    )
    private void champions$extractTexture(ShulkerBullet entity, ShulkerBulletRenderState state, float partialTicks, CallbackInfo ci) {
        IBullet bullet = (IBullet) entity;

        Identifier texture = null;
        if (bullet.champions$isArctic()) {
            texture = ARCTIC_TEXTURE;
        } else if (bullet.champions$isMolten()) {
            texture = MOLTEN_TEXTURE;
        }
        state.setData(CHAMPIONS_TEXTURE, texture);
    }

    /**
     * Change bullet color
     * @param state render state with texture from IBullet
     * @return texture
     */
    @Redirect(
            method = SUBMIT,
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/entity/ShulkerBulletRenderer;TEXTURE_LOCATION:Lnet/minecraft/resources/Identifier;",
                    opcode = org.objectweb.asm.Opcodes.GETSTATIC
            )
    )
    private Identifier redirectGetLayer(ShulkerBulletRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        Identifier texture = state.getData(CHAMPIONS_TEXTURE);
        return texture != null ? texture : DEFAULT_TEXTURE;
    }

    @Redirect(
            method = SUBMIT,
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/entity/ShulkerBulletRenderer;RENDER_TYPE:Lnet/minecraft/client/renderer/rendertype/RenderType;",
                    opcode = org.objectweb.asm.Opcodes.GETSTATIC
            )
    )
    private RenderType redirectLayerField(ShulkerBulletRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        Identifier texture = state.getData(CHAMPIONS_TEXTURE);
        return RenderTypes.entityTranslucent(texture != null ? texture : DEFAULT_TEXTURE);
    }
}
