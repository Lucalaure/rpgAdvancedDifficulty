package crystal.champions.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Blocks placed by affixes (e.g. Webslinger's cobwebs) that remove themselves after a while.
 * Anything still pending when the server stops is removed then, so nothing is left behind.
 */
public final class TemporaryBlocks {
    private TemporaryBlocks() {
    }

    private record Entry(ResourceKey<Level> dimension, BlockPos pos, Block block, long removeAt) {
    }

    private static final List<Entry> ENTRIES = new ArrayList<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> removeExpired(server, false));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> removeExpired(server, true));
    }

    /** Places the block only if the position is empty (never replaces anything). */
    public static boolean place(ServerLevel level, BlockPos pos, BlockState state, int ticks) {
        if (!level.getBlockState(pos).isAir()) return false;
        level.setBlockAndUpdate(pos, state);
        ENTRIES.add(new Entry(level.dimension(), pos.immutable(), state.getBlock(), level.getGameTime() + ticks));
        return true;
    }

    private static void removeExpired(MinecraftServer server, boolean all) {
        Iterator<Entry> it = ENTRIES.iterator();
        while (it.hasNext()) {
            Entry entry = it.next();
            ServerLevel level = server.getLevel(entry.dimension());
            if (level == null) {
                it.remove();
                continue;
            }
            if (all || level.getGameTime() >= entry.removeAt()) {
                if (level.getBlockState(entry.pos()).is(entry.block())) {
                    level.removeBlock(entry.pos(), false);
                }
                it.remove();
            }
        }
    }
}
