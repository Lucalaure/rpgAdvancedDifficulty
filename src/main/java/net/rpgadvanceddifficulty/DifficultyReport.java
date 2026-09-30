package net.rpgadvanceddifficulty;

import crystal.champions.util.ChampionRank;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.rpgdifficulty.RpgDifficultyMain;
import net.rpgdifficulty.config.RpgDifficultyConfig;
import net.rpgdifficulty.data.DifficultyLoader;
import net.rpgdifficulty.zone.DifficultyZone;
import net.rpgdifficulty.zone.DifficultyZonePersistentState;

import java.util.HashMap;

/**
 * The difficulty a normal (non-boss) mob spawning at a position would get, for the F3 readout.
 * Mirrors MobStrengthener.changeAttributes: zones, dimension datapacks, time, distance and game difficulty.
 */
public final class DifficultyReport {
    private DifficultyReport() {
    }

    /**
     * @param factor        health/damage multiplier a mob spawning here gets (1.0 = vanilla)
     * @param maxFactor     highest the factor can go here on this difficulty
     * @param championChance chance (0-1) that a hostile mob spawning here becomes a champion
     * @param minutesToNext minutes of world time until the next increase, or -1 if it can't increase
     * @param inZone        true if a difficulty zone sets the factor here
     */
    public record Snapshot(float factor, float maxFactor, float championChance, int minutesToNext, boolean inZone) {
    }

    public static Snapshot compute(ServerLevel world, Vec3 pos) {
        RpgDifficultyConfig config = RpgDifficultyMain.CONFIG;
        String dimension = world.dimension().identifier().toString();
        HashMap<String, Object> map = DifficultyLoader.dimensionDifficulty.get(dimension);
        boolean overworld = world.dimension() == Level.OVERWORLD;

        double growth = DifficultyModes.growth(world);
        double maxFactor = DifficultyModes.cap(world) * (map != null ? (double) map.get("maxFactorHealth") : config.maxFactorHealth);
        double factor;
        int minutesToNext = -1;

        DifficultyZone zone = DifficultyZonePersistentState.get(world.getServer()).findZone(dimension, pos.x, pos.y, pos.z).orElse(null);
        if (zone != null) {
            factor = zone.getFactor();
        } else {
            factor = map != null ? (double) map.get("startingFactor") : config.startingFactor;

            // Distance
            int increasingDistance = map != null ? (int) map.get("increasingDistance") : config.increasingDistance;
            if (config.enableDistanceScaling && increasingDistance != 0 && !(config.excludeDistanceInOtherDimension && !overworld)) {
                int spawnX = map != null && map.containsKey("distanceCoordinatesX") ? (int) map.get("distanceCoordinatesX") : world.getRespawnData().pos().getX();
                int spawnZ = map != null && map.containsKey("distanceCoordinatesZ") ? (int) map.get("distanceCoordinatesZ") : world.getRespawnData().pos().getZ();
                int startingDistance = map != null ? (int) map.get("startingDistance") : config.startingDistance;
                float distance = Mth.sqrt((float) pos.distanceToSqr(spawnX, pos.y, spawnZ));
                distance = (int) distance <= startingDistance ? 0 : distance - startingDistance;
                factor += ((int) distance / increasingDistance) * growth * (map != null ? (double) map.get("distanceFactor") : config.distanceFactor);
            }

            // Time
            int increasingTime = map != null ? (int) map.get("increasingTime") : config.increasingTime;
            if (increasingTime != 0 && !(config.excludeTimeInOtherDimension && !overworld)) {
                long startingTicks = (long) (map != null ? (int) map.get("startingTime") : config.startingTime) * 1200L;
                long stepTicks = increasingTime * 1200L;
                long elapsed = Math.max(0L, world.getGameTime() - startingTicks);
                long steps = elapsed / stepTicks;
                factor += steps * growth * (map != null ? (double) map.get("timeFactor") : config.timeFactor);
                long nextStepAt = startingTicks + (steps + 1) * stepTicks;
                minutesToNext = (int) Math.ceil((nextStepAt - world.getGameTime()) / 1200.0);
            }

            factor = Math.min(factor, maxFactor);
            if (factor >= maxFactor) minutesToNext = -1;
        }
        factor = Math.round(factor * 100.0) / 100.0;

        // Same odds as the real champion roll for a hostile mob spawning here
        double progress = config.championsScaleWithDifficulty ? Math.max(0.0, factor - 1.0) : 0.0;
        double[] weights = ChampionRank.tierWeights(progress, config.championChanceScaling, config.maxChampionChanceMultiplier,
                Integer.MAX_VALUE, tier -> DifficultyModes.tierMultiplier(world, tier));
        double total = 0;
        double champions = 0;
        for (int i = 0; i < weights.length; i++) {
            total += weights[i];
            if (ChampionRank.RANKS.get(i).tier() > 0) champions += weights[i];
        }
        float championChance = config.enableChampions && total > 0 ? (float) (champions / total) : 0.0F;

        return new Snapshot((float) factor, (float) maxFactor, championChance, minutesToNext, zone != null);
    }
}
