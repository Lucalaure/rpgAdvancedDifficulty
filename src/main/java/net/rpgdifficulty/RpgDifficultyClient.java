package net.rpgdifficulty;

import net.rpgadvanceddifficulty.client.DifficultyDebugEntry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.rpgdifficulty.zone.ClientZoneTracker;

@Environment(EnvType.CLIENT)
public class RpgDifficultyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DifficultyDebugEntry.register();
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("rpgdifficulty", "hud_testing"), (drawContext, tickDelta) -> {
            Minecraft client = Minecraft.getInstance();
            if (RpgDifficultyMain.CONFIG.hudTesting && !client.gui.hud.isHidden() && client.hitResult != null && client.hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) client.hitResult).getEntity();
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) ((EntityHitResult) client.hitResult).getEntity();
                    int scaledWidth = drawContext.guiWidth();
                    int scaledHeight = drawContext.guiHeight();
                    drawContext.text(client.font, BuiltInRegistries.ENTITY_TYPE.getKey(livingEntity.getType()).toString(), (int) (scaledWidth * 0.01F), (int) (scaledHeight * 0.95F),
                            0xFFFFFFFF);
                    drawContext.text(client.font, "Health: " + livingEntity.getHealth(), (int) (scaledWidth * 0.01F), (int) (scaledHeight * 0.91F), 0xFFFFFFFF);
                }
            }
        });
        ClientZoneTracker.init();
    }

}
