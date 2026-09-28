package crystal.champions.client.mixin;

import crystal.champions.IBullet;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.ShulkerBulletEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShulkerBulletEntityRenderer.class)
public class ShulkerBulletRendererMixin {

    @Unique private static final Identifier ARCTIC_TEXTURE = Identifier.of("champions", "textures/entity/arctic.png");
    @Unique private static final Identifier MOLTEN_TEXTURE = Identifier.of("champions", "textures/entity/molten.png");

    /**
     * Change bullet color
     * @param model which model
     * @param texture idk pray
     * @param entity for check IBullet
     * @return texture
     */
    @Redirect(
            method = "render*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/ShulkerBulletEntityModel;getLayer(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"
            )
    )
    private RenderLayer redirectGetLayer(ShulkerBulletEntityModel model, Identifier texture, ShulkerBulletEntity entity) {
        IBullet bullet = (IBullet) entity;

        if (bullet.champions$isArctic()) {
            return model.getLayer(ARCTIC_TEXTURE);
        } else if (bullet.champions$isMolten()) {
            return model.getLayer(MOLTEN_TEXTURE);
        }

        return model.getLayer(texture);
    }

    @Redirect(
            method = "render*",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/entity/ShulkerBulletEntityRenderer;LAYER:Lnet/minecraft/client/render/RenderLayer;",
                    opcode = org.objectweb.asm.Opcodes.GETSTATIC
            )
    )
    private RenderLayer redirectLayerField(ShulkerBulletEntity entity) {
        IBullet bullet = (IBullet) entity;

        if (bullet.champions$isArctic()) {
            return RenderLayer.getEntityTranslucent(ARCTIC_TEXTURE);
        } else if (bullet.champions$isMolten()) {
            return RenderLayer.getEntityTranslucent(MOLTEN_TEXTURE);
        }
        return RenderLayer.getEntityTranslucent(Identifier.of("minecraft","textures/entity/shulker/spark.png"));
    }
}