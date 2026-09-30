package net.rpgdifficulty.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.resources.Identifier;
import net.rpgadvanceddifficulty.client.DifficultyDebugEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Shows the RPG Difficulty F3 lines by default. Players can still turn them off in the F3 debug options,
 * which saves an explicit status that this won't override.
 */
@Environment(EnvType.CLIENT)
@Mixin(DebugScreenEntryList.class)
public class DebugScreenEntryListMixin {
    @Shadow @Final private Map<Identifier, DebugScreenEntryStatus> allStatuses;

    @Inject(method = "resetStatuses", at = @At("TAIL"))
    private void rpgdifficulty$showByDefault(Map<Identifier, DebugScreenEntryStatus> newEntries, CallbackInfo ci) {
        this.allStatuses.putIfAbsent(DifficultyDebugEntry.ID, DebugScreenEntryStatus.IN_OVERLAY);
    }
}
