package crystal.champions.client.mixin;

import crystal.champions.client.net.ClientPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public abstract class BossBarClientMixin {
    /**
     * Убираем все босс бары если есть чемпионы
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancelRender(GuiGraphics context, CallbackInfo ci) {
        if (!ClientPacket.activeChampions.isEmpty()) {
            ci.cancel();
        }
    }
}
