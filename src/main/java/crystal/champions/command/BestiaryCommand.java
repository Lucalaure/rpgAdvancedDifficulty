package crystal.champions.command;

import com.mojang.brigadier.CommandDispatcher;
import crystal.champions.bestiary.Bestiary;
import crystal.champions.bestiary.BestiaryItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * /bestiary                  - gives yourself a bestiary book (any player)
 * /bestiary give <players>   - gives other players one (operators)
 * /bestiary reset [players]  - forgets all discovered affixes, yours or the given players' (operators)
 * Discoveries are stored on the player, so a new copy shows everything already found.
 */
public final class BestiaryCommand {
    private BestiaryCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("bestiary")
                .executes(c -> give(c.getSource(), java.util.List.of(c.getSource().getPlayerOrException())))
                .then(literal("give")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(argument("players", EntityArgument.players())
                                .executes(c -> give(c.getSource(), EntityArgument.getPlayers(c, "players")))))
                .then(literal("reset")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(c -> reset(c.getSource(), java.util.List.of(c.getSource().getPlayerOrException())))
                        .then(argument("players", EntityArgument.players())
                                .executes(c -> reset(c.getSource(), EntityArgument.getPlayers(c, "players"))))));
    }

    private static int reset(CommandSourceStack source, Collection<ServerPlayer> players) {
        players.forEach(Bestiary::reset);
        source.sendSuccess(() -> players.size() == 1
                ? Component.translatable("champions.bestiary.reset", players.iterator().next().getDisplayName())
                : Component.translatable("champions.bestiary.reset_many", players.size()), true);
        return players.size();
    }

    private static int give(CommandSourceStack source, Collection<ServerPlayer> players) {
        players.forEach(BestiaryItem::give);
        source.sendSuccess(() -> players.size() == 1
                ? Component.translatable("champions.bestiary.given", players.iterator().next().getDisplayName())
                : Component.translatable("champions.bestiary.given_many", players.size()), true);
        return players.size();
    }
}
