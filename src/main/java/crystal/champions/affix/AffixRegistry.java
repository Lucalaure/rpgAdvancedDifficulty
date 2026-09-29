package crystal.champions.affix;

import crystal.champions.Champions;
import crystal.champions.config.ChampionsConfigAffixes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Регистрация всех аффиксов, чтобы не по отдельности их делать
 * Each affix is listed with its name (config keys "<name>_affix" / "<name>_tier", translation key "affix.<name>") and default minimum tier.
 */
public class AffixRegistry {
    private AffixRegistry() {
        /* This utility class should not be instantiated */
    }
    public static final Map<String, Affix> ALL_AFFIXES = new LinkedHashMap<>();

    /** An affix with its default minimum champion tier (more powerful affixes need a higher tier). */
    public record Entry(String name, int tier, Supplier<Affix> factory) {
    }

    private static final List<Entry> FACTORY_LIST = List.of(
            new Entry("hasty", 1, HastyAffix::new),
            new Entry("arctic", 3, ArcticAffix::new),
            new Entry("molten", 3, MoltenAffix::new),
            new Entry("desecrating", 3, DesecratingAffix::new),
            new Entry("plagued", 2, PlaguedAffix::new),
            new Entry("infected", 2, InfectedAffix::new),
            new Entry("adaptive", 2, AdaptiveAffix::new),
            new Entry("knocking", 1, KnockingAffix::new),
            new Entry("shielding", 4, ShieldingAffix::new),
            new Entry("reflection", 3, ReflectionAffix::new),
            new Entry("dampening", 1, DampingAffix::new),
            new Entry("lively", 1, LivelyAffix::new),
            new Entry("blinded", 1, BlindedAffix::new),
            new Entry("paralyzing", 3, ParalyzingAffix::new),
            new Entry("big", 1, BigAffix::new),
            // Mob-specific affixes
            new Entry("horde_caller", 2, HordeCallerAffix::new),
            new Entry("sunproof", 1, SunproofAffix::new),
            new Entry("sniper", 3, SniperAffix::new),
            new Entry("volley", 2, VolleyAffix::new),
            new Entry("frost_archer", 2, FrostArcherAffix::new),
            new Entry("stalker", 3, StalkerAffix::new),
            new Entry("webslinger", 2, WebslingerAffix::new),
            new Entry("brood_mother", 3, BroodMotherAffix::new),
            new Entry("pouncer", 1, PouncerAffix::new),
            new Entry("blink", 3, BlinkAffix::new),
            new Entry("thief", 1, ThiefAffix::new),
            new Entry("alchemist", 3, AlchemistAffix::new),
            new Entry("coven", 2, CovenAffix::new),
            new Entry("inferno", 3, InfernoAffix::new),
            new Entry("barrage", 4, BarrageAffix::new),
            new Entry("splitter", 3, SplitterAffix::new),
            new Entry("sticky", 1, StickyAffix::new),
            new Entry("warlord", 3, WarlordAffix::new),
            new Entry("berserker", 4, BerserkerAffix::new)
            );

    /** Every affix name, enabled or not (config toggles, config screen, commands). */
    public static final List<String> NAMES = FACTORY_LIST.stream().map(Entry::name).toList();

    public static int defaultTier(String name) {
        return FACTORY_LIST.stream().filter(entry -> entry.name().equals(name)).mapToInt(Entry::tier).findFirst().orElse(1);
    }

    public static void affixesRegister() {
        ALL_AFFIXES.clear();
        ChampionsConfigAffixes config = ChampionsConfigAffixes.get();
        for (Entry entry : FACTORY_LIST) {
            if (config.isEnabled(entry.name())) {
                Affix affix = entry.factory().get();
                affix.setMinTier(config.minTier(entry.name()));
                ALL_AFFIXES.put(affix.getName(), affix);
            }
        }
        Champions.LOGGER.info("Registered {} champion affixes", ALL_AFFIXES.size());
    }
}
