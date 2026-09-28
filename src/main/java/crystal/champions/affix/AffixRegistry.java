package crystal.champions.affix;

import crystal.champions.Champions;
import crystal.champions.config.ChampionsConfigAffixes;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Регистрация всех аффиксов, чтобы не по отдельности их делать
 */
public class AffixRegistry {
    private AffixRegistry() {
        /* This utility class should not be instantiated */
    }
    public static final Map<String, Affix> ALL_AFFIXES = new LinkedHashMap<>();

    private static final List<Supplier<Affix>> FACTORY_LIST = List.of(
            HastyAffix::new,
            ArcticAffix::new,
            MoltenAffix::new,
            DesecratingAffix::new,
            PlaguedAffix::new,
            InfectedAffix::new,
            AdaptiveAffix::new,
            KnockingAffix::new,
            ShieldingAffix::new,
            ReflectionAffix::new,
            MagneticAffix::new,
            DampingAffix::new,
            LivelyAffix::new,
            BlindedAffix::new,
            ParalyzingAffix::new
            );

    public static void affixesRegister() {
        ALL_AFFIXES.clear();
        ChampionsConfigAffixes config = ChampionsConfigAffixes.get();
        for (int i = 0; i < FACTORY_LIST.size(); i++) {
            String fieldName = "r" + (i + 1);
            try {
                Field field = config.getClass().getField(fieldName);
                boolean isEnabled = (boolean) field.get(config);
                if (isEnabled) {
                    Affix affix = FACTORY_LIST.get(i).get();
                    ALL_AFFIXES.put(affix.getName(), affix);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Champions.LOGGER.error("Config error, can not find: {}", fieldName);
            }
        }
        Champions.LOGGER.info("Registered {} champion affixes", ALL_AFFIXES.size());
    }
}