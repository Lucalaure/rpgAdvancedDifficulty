package net.rpgadvanceddifficulty;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.ServerLevelData;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Testing commands (operators only) for the world time that drives difficulty scaling:
 * /rpgdifficulty time query         - world age in hours and the difficulty where you stand
 * /rpgdifficulty time add <hours>   - skip world time forward (doesn't change the time of day)
 * /rpgdifficulty time set <hours>   - set the world age
 */
public final class DifficultyTimeCommand {
    private DifficultyTimeCommand() {
    }

    private static final long TICKS_PER_HOUR = 72000L;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("rpgdifficulty")
                .then(literal("time")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(literal("query").executes(c -> query(c.getSource())))
                        .then(literal("add").then(argument("hours", DoubleArgumentType.doubleArg())
                                .executes(c -> setAge(c.getSource(), worldAge(c.getSource().getServer()) + hours(c.getSource(), DoubleArgumentType.getDouble(c, "hours"))))))
                        .then(literal("set").then(argument("hours", DoubleArgumentType.doubleArg(0))
                                .executes(c -> setAge(c.getSource(), hours(c.getSource(), DoubleArgumentType.getDouble(c, "hours"))))))));
    }

    private static long hours(CommandSourceStack source, double hours) {
        return Math.round(hours * TICKS_PER_HOUR);
    }

    private static long worldAge(MinecraftServer server) {
        return server.overworld().getGameTime();
    }

    private static int setAge(CommandSourceStack source, long ticks) {
        // Game time is shared by all dimensions and stored in the overworld's level data
        ((ServerLevelData) source.getServer().overworld().getLevelData()).setGameTime(Math.max(0L, ticks));
        return query(source);
    }

    private static int query(CommandSourceStack source) {
        double hours = worldAge(source.getServer()) / (double) TICKS_PER_HOUR;
        DifficultyReport.Snapshot snapshot = DifficultyReport.compute(source.getLevel(), source.getPosition());
        source.sendSuccess(() -> Component.literal(String.format("World age: %.1f hours. Difficulty here: %.2fx (max %.2fx), champion chance %.1f%%",
                hours, snapshot.factor(), snapshot.maxFactor(), snapshot.championChance() * 100.0)), false);
        return (int) Math.round(hours);
    }
}
