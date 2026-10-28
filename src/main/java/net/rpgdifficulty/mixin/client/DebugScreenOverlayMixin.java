package net.rpgdifficulty.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.rpgadvanceddifficulty.client.DifficultyDebugEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Adds the RPG Difficulty lines to the left side of the F3 screen.
 */
@Environment(EnvType.CLIENT)
@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
    @Inject(method = "getGameInformation", at = @At("RETURN"))
    private void rpgdifficulty$addDifficultyLines(CallbackInfoReturnable<List<String>> cir) {
        List<String> lines = DifficultyDebugEntry.lines();
        if (!lines.isEmpty()) {
            cir.getReturnValue().add("");
            cir.getReturnValue().addAll(lines);
        }
    }
}
