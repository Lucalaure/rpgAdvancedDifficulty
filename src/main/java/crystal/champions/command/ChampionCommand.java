package crystal.champions.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import crystal.champions.IChampions;
import crystal.champions.affix.Affix;
import crystal.champions.affix.AffixRegistry;
import crystal.champions.util.ChampionRank;
import crystal.champions.util.PrepareChampions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Testing commands (operators only):
 * /champion list                              - all enabled affixes and which mobs can have them
 * /champion demo <affix>                      - spawns a fitting mob with just that affix in front of you
 * /champion spawn <mob> <affix> [affix...]    - any mob with any affixes (tier = number of affixes or the highest affix tier, max 5)
 */
public final class ChampionCommand {
    private ChampionCommand() {
    }

    private static final DynamicCommandExceptionType UNKNOWN_AFFIX = new DynamicCommandExceptionType(
            name -> Component.literal("Unknown or disabled affix: " + name));
    private static final DynamicCommandExceptionType NOT_A_MOB = new DynamicCommandExceptionType(
            type -> Component.literal(type + " can't be a champion"));
    private static final DynamicCommandExceptionType WRONG_MOB = new DynamicCommandExceptionType(
            name -> Component.literal("That mob can't have the affix " + name));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(literal("champion")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("list").executes(c -> list(c.getSource())))
                .then(literal("demo")
                        .then(argument("affix", StringArgumentType.word())
                                .suggests((c, builder) -> SharedSuggestionProvider.suggest(AffixRegistry.ALL_AFFIXES.keySet(), builder))
                                .executes(c -> demo(c.getSource(), StringArgumentType.getString(c, "affix")))))
                .then(literal("spawn")
                        .then(argument("mob", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
                                .suggests(SuggestionProviders.cast(SuggestionProviders.SUMMONABLE_ENTITIES))
                                .then(argument("affixes", StringArgumentType.greedyString())
                                        .suggests((c, builder) -> suggestAffixes(builder))
                                        .executes(c -> spawn(c.getSource(),
                                                ResourceArgument.getSummonableEntityType(c, "mob").value(),
                                                parseAffixes(StringArgumentType.getString(c, "affixes"))))))));
    }

    private static int list(CommandSourceStack source) {
        MutableComponent message = Component.literal("Champion affixes:").withStyle(ChatFormatting.GOLD);
        for (Affix affix : AffixRegistry.ALL_AFFIXES.values()) {
            message.append(Component.literal("\n" + affix.getName()).withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal(" - ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal("tier " + affix.getMinTier() + "+, ").withStyle(ChatFormatting.GRAY))
                    .append(Component.translatable(affix.getMobsKey()).withStyle(ChatFormatting.GRAY));
        }
        source.sendSuccess(() -> message, false);
        return AffixRegistry.ALL_AFFIXES.size();
    }

    private static int demo(CommandSourceStack source, String affixName) throws CommandSyntaxException {
        Affix affix = AffixRegistry.ALL_AFFIXES.get(affixName);
        if (affix == null) throw UNKNOWN_AFFIX.create(affixName);
        return spawn(source, affix.getExampleMob(), List.of(affixName));
    }

    private static int spawn(CommandSourceStack source, EntityType<?> type, List<String> affixNames) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        if (!(type.create(level, EntitySpawnReason.COMMAND) instanceof Mob mob)) throw NOT_A_MOB.create(type.getDescription().getString());

        for (String name : affixNames) {
            Affix affix = AffixRegistry.ALL_AFFIXES.get(name);
            if (affix == null) throw UNKNOWN_AFFIX.create(name);
            if (!affix.canApplyTo(mob)) throw WRONG_MOB.create(name);
        }

        Vec3 pos = spawnPosition(source);
        mob.snapTo(pos.x, pos.y, pos.z, source.getRotation().y + 180.0F, 0.0F);
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(pos)), EntitySpawnReason.COMMAND, null);

        // At least the number of affixes and at least the highest affix tier (max 5)
        int highestAffixTier = affixNames.stream().mapToInt(name -> AffixRegistry.ALL_AFFIXES.get(name).getMinTier()).max().orElse(1);
        int tier = Math.max(1, Math.min(Math.max(affixNames.size(), highestAffixTier), ChampionRank.RANKS.size() - 1));
        IChampions champion = (IChampions) mob;
        champion.champions$setChampionTier(tier);
        champion.champions$setAffixesString(String.join(",", affixNames));
        PrepareChampions.prepareAttributes(mob, ChampionRank.RANKS.get(tier));
        champion.champions$getActiveAffixes().forEach(affix -> affix.onApply(mob));

        level.addFreshEntity(mob);
        mob.setHealth(mob.getMaxHealth());

        source.sendSuccess(() -> Component.literal("Spawned tier " + tier + " ")
                .append(mob.getType().getDescription())
                .append(" with " + String.join(", ", affixNames)), true);
        return 1;
    }

    /** Three blocks in front of the command user (their feet level). */
    private static Vec3 spawnPosition(CommandSourceStack source) {
        Entity entity = source.getEntity();
        if (entity == null) return source.getPosition();
        Vec3 look = entity.getLookAngle().multiply(1, 0, 1);
        return look.lengthSqr() < 1.0E-4 ? entity.position() : entity.position().add(look.normalize().scale(3.0));
    }

    private static List<String> parseAffixes(String input) {
        return new ArrayList<>(Arrays.stream(input.split("[\\s,]+")).filter(s -> !s.isEmpty()).distinct().toList());
    }

    // Suggests affix names for the word being typed, after any already entered
    private static CompletableFuture<Suggestions> suggestAffixes(SuggestionsBuilder builder) {
        String remaining = builder.getRemaining();
        int lastSeparator = Math.max(remaining.lastIndexOf(' '), remaining.lastIndexOf(','));
        return SharedSuggestionProvider.suggest(AffixRegistry.ALL_AFFIXES.keySet(), builder.createOffset(builder.getStart() + lastSeparator + 1));
    }
}
