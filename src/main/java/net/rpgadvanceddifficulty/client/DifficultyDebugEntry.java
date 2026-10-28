package net.rpgadvanceddifficulty.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.rpgadvanceddifficulty.DifficultySync;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * "RPG Difficulty" lines on the F3 screen: the difficulty a mob spawning where you stand would get,
 * how high it can go, the champion chance, and when it next increases. Values come from the server once a second.
 * Added to the F3 screen by DebugScreenOverlayMixin.
 */
@Environment(EnvType.CLIENT)
public class DifficultyDebugEntry {
    private static DifficultySync.@Nullable DifficultyInfo latest;

    /** Latest values from the server, or null before the first update. */
    public static DifficultySync.@Nullable DifficultyInfo latest() {
        return latest;
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(DifficultySync.DifficultyInfo.TYPE, (payload, context) -> latest = payload);
    }

    /** The F3 lines, or an empty list before the first update. */
    public static List<String> lines() {
        DifficultySync.DifficultyInfo info = latest;
        if (info == null) return List.of();

        String next = info.inZone() ? "set by a difficulty zone"
                : info.minutesToNext() < 0 ? "at max"
                : "next increase in " + formatMinutes(info.minutesToNext());
        return List.of(
                String.format(Locale.ROOT, "RPG Difficulty: %.2fx / %.2fx max (%s)", info.factor(), info.maxFactor(), next),
                String.format(Locale.ROOT, "Champion chance: %.1f%% of hostile spawns", info.championChance() * 100.0));
    }

    private static String formatMinutes(int minutes) {
        return minutes >= 60 ? String.format(Locale.ROOT, "%dh %dm", minutes / 60, minutes % 60) : minutes + "m";
    }
}
