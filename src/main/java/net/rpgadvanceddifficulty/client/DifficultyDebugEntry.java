package net.rpgadvanceddifficulty.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.rpgadvanceddifficulty.DifficultySync;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

/**
 * "RPG Difficulty" lines on the F3 screen: the difficulty a mob spawning where you stand would get,
 * how high it can go, the champion chance, and when it next increases. Values come from the server once a second.
 * Can be toggled in the F3 debug options like any other entry.
 */
@Environment(EnvType.CLIENT)
public class DifficultyDebugEntry implements DebugScreenEntry {
    public static final Identifier ID = Identifier.fromNamespaceAndPath("rpgadvanceddifficulty", "difficulty");

    private static DifficultySync.@Nullable DifficultyInfo latest;

    /** Latest values from the server, or null before the first update. */
    public static DifficultySync.@Nullable DifficultyInfo latest() {
        return latest;
    }

    public static void register() {
        DebugScreenEntries.register(ID, new DifficultyDebugEntry());
        ClientPlayNetworking.registerGlobalReceiver(DifficultySync.DifficultyInfo.TYPE, (payload, context) -> latest = payload);
    }

    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel, @Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
        DifficultySync.DifficultyInfo info = latest;
        if (info == null) return;

        String next = info.inZone() ? "set by a difficulty zone"
                : info.minutesToNext() < 0 ? "at max"
                : "next increase in " + formatMinutes(info.minutesToNext());
        displayer.addLine(String.format(Locale.ROOT, "RPG Difficulty: %.2fx / %.2fx max (%s)", info.factor(), info.maxFactor(), next));
        displayer.addLine(String.format(Locale.ROOT, "Champion chance: %.1f%% of hostile spawns", info.championChance() * 100.0));
    }

    private static String formatMinutes(int minutes) {
        return minutes >= 60 ? String.format(Locale.ROOT, "%dh %dm", minutes / 60, minutes % 60) : minutes + "m";
    }
}
