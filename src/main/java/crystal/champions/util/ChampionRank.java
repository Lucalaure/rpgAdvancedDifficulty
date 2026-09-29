package crystal.champions.util;

import crystal.champions.config.ChampionsConfigServer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntToDoubleFunction;

/** slots = affix slot budget for this tier. */
public record ChampionRank(int tier, int slots, int weight, float growth_h, float growth_s) {

    public static List<ChampionRank> RANKS = new ArrayList<>();
    private static int TOTAL_WEIGHT = 0;

    public static void get() {
        var cfg = ChampionsConfigServer.get();
        TOTAL_WEIGHT = 0;
        RANKS = List.of(
                new ChampionRank(0, 0, cfg.w0, 1.0f, 1.0f),
                new ChampionRank(1, cfg.a1, cfg.w1, cfg.gh1, cfg.gs1),
                new ChampionRank(2, cfg.a2, cfg.w2, cfg.gh2, cfg.gs2),
                new ChampionRank(3, cfg.a3, cfg.w3, cfg.gh3, cfg.gs3),
                new ChampionRank(4, cfg.a4, cfg.w4, cfg.gh4, cfg.gs4),
                new ChampionRank(5, cfg.a5, cfg.w5, cfg.gh5, cfg.gs5)
        );

        for (ChampionRank r : RANKS) {
            if (r.weight() > 0) TOTAL_WEIGHT += r.weight();
        }
    }

    public static ChampionRank getRandomRank(RandomSource random) {
        if (TOTAL_WEIGHT <= 0) return RANKS.getFirst();

        int roll = random.nextInt(TOTAL_WEIGHT);
        for (ChampionRank rank : RANKS) {
            if (roll < rank.weight()) return rank;
            roll -= rank.weight();
        }
        return RANKS.getFirst();
    }

    /**
     * Weight of each tier (index = position in RANKS). Champion tiers become more likely as
     * difficulty rises: each tier's weight is multiplied by min(maxMultiplier, 1 + progress * scaling * tier),
     * so higher tiers gain the most, then by the game difficulty multiplier for that tier.
     * Tiers above maxTier get weight 0.
     */
    public static double[] tierWeights(double progress, double scaling, double maxMultiplier, int maxTier, IntToDoubleFunction difficultyMultiplier) {
        double[] weights = new double[RANKS.size()];
        for (int i = 0; i < RANKS.size(); i++) {
            ChampionRank rank = RANKS.get(i);
            if (rank.weight() <= 0 || rank.tier() > maxTier) continue;
            double multiplier = rank.tier() == 0 ? 1.0
                    : Math.min(maxMultiplier, 1.0 + progress * scaling * rank.tier()) * difficultyMultiplier.applyAsDouble(rank.tier());
            weights[i] = rank.weight() * Math.max(0.0, multiplier);
        }
        return weights;
    }

    public static ChampionRank getRandomRank(RandomSource random, double[] weights) {
        double total = 0;
        for (double weight : weights) total += weight;
        if (total <= 0) return RANKS.getFirst();

        double roll = random.nextDouble() * total;
        for (int i = 0; i < RANKS.size(); i++) {
            if (roll < weights[i]) return RANKS.get(i);
            roll -= weights[i];
        }
        return RANKS.getFirst();
    }
}
