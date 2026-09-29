package crystal.champions;

import crystal.champions.affix.Affix;

import java.util.List;

public interface IChampions {
    /** Tier value for mobs that must never roll as a champion (e.g. Infested silverfish minions). */
    int NEVER_CHAMPION = -1;

    int champions$getChampionTier();
    void champions$setChampionTier(int tier);
    // affixes
    String champions$getAffixesString();
    void champions$setAffixesString(String affixes);
    List<Affix> champions$getActiveAffixes();
    default boolean champions$hasAffix(String name) {
        for (String affix : champions$getAffixesString().split(",")) {
            if (affix.equals(name)) return true;
        }
        return false;
    }
    // adaptive
    String champions$getAdaptationType();
    void champions$setAdaptationType(String type);
    int champions$getAdaptation();
    void champions$setAdaptation(int count);

    boolean champions$isShielding();

    /** False for champions that must not drop champion loot (e.g. Splitter pieces). */
    boolean champions$dropsChampionLoot();
    void champions$setDropsChampionLoot(boolean drops);
    default void champions$setShielding(boolean value) {}
}
