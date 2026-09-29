package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import crystal.champions.affix.Affix;
import crystal.champions.affix.AffixRegistry;
import crystal.champions.affix.SplitterAffix;
import crystal.champions.util.ChampionRank;
import crystal.champions.util.PrepareChampions;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

/**
 * Splitter: more pieces on death; some pieces keep one of the parent's other affixes
 * (as a champion of the lowest tier with enough slots) but never drop champion loot.
 */
@Mixin(AbstractCubeMob.class)
public class SplitterMixin {

    @Unique
    private boolean champions$isSplitter() {
        return this instanceof IChampions champion && champion.champions$hasAffix("splitter");
    }

    @Inject(method = "getSplitCount", at = @At("RETURN"), cancellable = true)
    private void champions$morePieces(CallbackInfoReturnable<Integer> cir) {
        if (champions$isSplitter()) {
            cir.setReturnValue(cir.getReturnValue() + SplitterAffix.EXTRA_PIECES);
        }
    }

    // Runs before the piece is added to the world, so it doesn't roll its own champion tier.
    // Each piece has a chance to keep one of the parent's other affixes (never Splitter, so it doesn't chain),
    // and pieces never drop champion loot.
    @Inject(method = "setUpSplitCube", at = @At("TAIL"))
    private void champions$inheritAffix(AbstractCubeMob cubeMob, int halfSize, float xd, float zd, CallbackInfo ci) {
        if (!champions$isSplitter()) return;
        IChampions piece = (IChampions) cubeMob;
        piece.champions$setChampionTier(IChampions.NEVER_CHAMPION);

        List<String> inheritable = Arrays.stream(((IChampions) this).champions$getAffixesString().split(","))
                .filter(name -> !name.equals("splitter") && AffixRegistry.ALL_AFFIXES.containsKey(name))
                .toList();
        if (inheritable.isEmpty() || cubeMob.getRandom().nextFloat() >= SplitterAffix.INHERIT_CHANCE) return;

        String inherited = inheritable.get(cubeMob.getRandom().nextInt(inheritable.size()));
        Affix affix = AffixRegistry.ALL_AFFIXES.get(inherited);
        // Lowest tier with enough slots for the inherited affix
        ChampionRank rank = PrepareChampions.lowestRankWithSlots(affix.getSlots());
        piece.champions$setChampionTier(rank.tier());
        piece.champions$setAffixesString(inherited);
        piece.champions$setDropsChampionLoot(false);
        PrepareChampions.prepareAttributes(cubeMob, rank);
        affix.onApply(cubeMob);
        cubeMob.setHealth(cubeMob.getMaxHealth());
    }
}
