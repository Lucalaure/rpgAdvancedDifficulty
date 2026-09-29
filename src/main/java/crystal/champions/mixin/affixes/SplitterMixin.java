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

/**
 * Splitter: more pieces on death, and each piece is a tier 1 champion with one of the parent's affixes.
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

    // Runs before the piece is added to the world, so it doesn't roll its own champion tier
    @Inject(method = "setUpSplitCube", at = @At("TAIL"))
    private void champions$inheritAffix(AbstractCubeMob cubeMob, int halfSize, float xd, float zd, CallbackInfo ci) {
        if (!champions$isSplitter()) return;
        String[] affixes = ((IChampions) this).champions$getAffixesString().split(",");
        String inherited = affixes[cubeMob.getRandom().nextInt(affixes.length)];

        IChampions piece = (IChampions) cubeMob;
        piece.champions$setChampionTier(1);
        piece.champions$setAffixesString(inherited);
        PrepareChampions.prepareAttributes(cubeMob, ChampionRank.RANKS.get(1));
        Affix affix = AffixRegistry.ALL_AFFIXES.get(inherited);
        if (affix != null) {
            affix.onApply(cubeMob);
        }
        cubeMob.setHealth(cubeMob.getMaxHealth());
    }
}
