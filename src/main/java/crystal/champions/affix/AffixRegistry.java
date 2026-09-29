package crystal.champions.affix;

import crystal.champions.Champions;
import crystal.champions.config.ChampionsConfigAffixes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Регистрация всех аффиксов, чтобы не по отдельности их делать
 * Each affix is listed with its name, which is also its config toggle ("<name>_affix") and translation key ("affix.<name>").
 */
public class AffixRegistry {
    private AffixRegistry() {
        /* This utility class should not be instantiated */
    }
    public static final Map<String, Affix> ALL_AFFIXES = new LinkedHashMap<>();

    private static final List<Map.Entry<String, Supplier<Affix>>> FACTORY_LIST = List.of(
            Map.entry("hasty", HastyAffix::new),
            Map.entry("arctic", ArcticAffix::new),
            Map.entry("molten", MoltenAffix::new),
            Map.entry("desecrating", DesecratingAffix::new),
            Map.entry("plagued", PlaguedAffix::new),
            Map.entry("infected", InfectedAffix::new),
            Map.entry("adaptive", AdaptiveAffix::new),
            Map.entry("knocking", KnockingAffix::new),
            Map.entry("shielding", ShieldingAffix::new),
            Map.entry("reflection", ReflectionAffix::new),
            Map.entry("dampening", DampingAffix::new),
            Map.entry("lively", LivelyAffix::new),
            Map.entry("blinded", BlindedAffix::new),
            Map.entry("paralyzing", ParalyzingAffix::new),
            Map.entry("big", BigAffix::new),
            // Mob-specific affixes
            Map.entry("horde_caller", HordeCallerAffix::new),
            Map.entry("sunproof", SunproofAffix::new),
            Map.entry("sniper", SniperAffix::new),
            Map.entry("volley", VolleyAffix::new),
            Map.entry("frost_archer", FrostArcherAffix::new),
            Map.entry("stalker", StalkerAffix::new),
            Map.entry("webslinger", WebslingerAffix::new),
            Map.entry("brood_mother", BroodMotherAffix::new),
            Map.entry("pouncer", PouncerAffix::new),
            Map.entry("blink", BlinkAffix::new),
            Map.entry("thief", ThiefAffix::new),
            Map.entry("alchemist", AlchemistAffix::new),
            Map.entry("coven", CovenAffix::new),
            Map.entry("inferno", InfernoAffix::new),
            Map.entry("barrage", BarrageAffix::new),
            Map.entry("splitter", SplitterAffix::new),
            Map.entry("sticky", StickyAffix::new),
            Map.entry("warlord", WarlordAffix::new),
            Map.entry("berserker", BerserkerAffix::new)
            );

    /** Every affix name, enabled or not (config toggles, config screen, commands). */
    public static final List<String> NAMES = FACTORY_LIST.stream().map(Map.Entry::getKey).toList();

    public static void affixesRegister() {
        ALL_AFFIXES.clear();
        ChampionsConfigAffixes config = ChampionsConfigAffixes.get();
        for (Map.Entry<String, Supplier<Affix>> entry : FACTORY_LIST) {
            if (config.isEnabled(entry.getKey())) {
                Affix affix = entry.getValue().get();
                ALL_AFFIXES.put(affix.getName(), affix);
            }
        }
        Champions.LOGGER.info("Registered {} champion affixes", ALL_AFFIXES.size());
    }
}
