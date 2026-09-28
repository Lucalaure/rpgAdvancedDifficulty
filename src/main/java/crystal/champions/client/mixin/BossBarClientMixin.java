package crystal.champions.client.mixin;

import crystal.champions.client.net.ClientPacket;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public abstract class BossBarClientMixin {
    /**
     * Убираем все босс бары если есть чемпионы
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancelRender(DrawContext context, CallbackInfo ci) {
        if (!ClientPacket.activeChampions.isEmpty()) {
            ci.cancel();
        }
    }
}
