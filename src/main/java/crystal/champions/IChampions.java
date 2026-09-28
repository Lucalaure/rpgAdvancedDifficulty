package crystal.champions;

import crystal.champions.affix.Affix;

import java.util.List;

public interface IChampions {
    int champions$getChampionTier();
    void champions$setChampionTier(int tier);
    // affixes
    String champions$getAffixesString();
    void champions$setAffixesString(String affixes);
    List<Affix> champions$getActiveAffixes();
    // adaptive
    String champions$getAdaptationType();
    void champions$setAdaptationType(String type);
    int champions$getAdaptation();
    void champions$setAdaptation(int count);

    boolean champions$isShielding();
    default void champions$setShielding(boolean value) {}
}
