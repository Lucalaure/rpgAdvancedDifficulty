package crystal.champions.client.mixin;

import crystal.champions.IBullet;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ShulkerBulletRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShulkerBulletRenderer.class)
public class ShulkerBulletRendererMixin {

    @Unique private static final ResourceLocation ARCTIC_TEXTURE = ResourceLocation.fromNamespaceAndPath("champions", "textures/entity/arctic.png");
    @Unique private static final ResourceLocation MOLTEN_TEXTURE = ResourceLocation.fromNamespaceAndPath("champions", "textures/entity/molten.png");
    @Unique private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/shulker/spark.png");

    @Unique private static final String RENDER =
            "render(Lnet/minecraft/world/entity/projectile/ShulkerBullet;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";

    /**
     * Texture override from IBullet (null = vanilla)
     */
    @Unique
    private static ResourceLocation champions$texture(ShulkerBullet entity) {
        IBullet bullet = (IBullet) entity;
        if (bullet.champions$isArctic()) {
            return ARCTIC_TEXTURE;
        } else if (bullet.champions$isMolten()) {
            return MOLTEN_TEXTURE;
        }
        return null;
    }

    /**
     * Change bullet color
     * @param model which model
     * @param texture vanilla texture
     * @param entity for check IBullet
     * @return render type
     */
    @Redirect(
            method = RENDER,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ShulkerBulletModel;renderType(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
            )
    )
    private RenderType redirectGetLayer(ShulkerBulletModel<ShulkerBullet> model, ResourceLocation texture, ShulkerBullet entity) {
        ResourceLocation override = champions$texture(entity);
        return model.renderType(override != null ? override : texture);
    }

    @Redirect(
            method = RENDER,
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/entity/ShulkerBulletRenderer;RENDER_TYPE:Lnet/minecraft/client/renderer/RenderType;",
                    opcode = org.objectweb.asm.Opcodes.GETSTATIC
            )
    )
    private RenderType redirectLayerField(ShulkerBullet entity) {
        ResourceLocation override = champions$texture(entity);
        return RenderType.entityTranslucent(override != null ? override : DEFAULT_TEXTURE);
    }
}
