package crystal.champions.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Explosions that go off a few ticks later with a smoking, hissing warning (Volatile).
 * They never break blocks. Pending ones are dropped if the server stops.
 */
public final class DelayedExplosions {
    private DelayedExplosions() {
    }

    private record Pending(ResourceKey<Level> dimension, Vec3 pos, float power, long explodeAt) {
    }

    private static final List<Pending> PENDING = new ArrayList<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(DelayedExplosions::tick);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> PENDING.clear());
    }

    public static void schedule(ServerLevel level, Vec3 pos, float power, int delayTicks) {
        PENDING.add(new Pending(level.dimension(), pos, power, level.getGameTime() + delayTicks));
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 1.0F, 0.5F);
    }

    private static void tick(MinecraftServer server) {
        Iterator<Pending> it = PENDING.iterator();
        while (it.hasNext()) {
            Pending pending = it.next();
            ServerLevel level = server.getLevel(pending.dimension());
            if (level == null) {
                it.remove();
                continue;
            }
            Vec3 pos = pending.pos();
            if (level.getGameTime() >= pending.explodeAt()) {
                level.explode(null, pos.x, pos.y, pos.z, pending.power(), Level.ExplosionInteraction.NONE);
                it.remove();
            } else if (level.getGameTime() % 2 == 0) {
                level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 4, 0.2, 0.3, 0.2, 0.02);
                level.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 1, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }
}
