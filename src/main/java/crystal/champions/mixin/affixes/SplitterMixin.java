package crystal.champions.mixin.affixes;

import crystal.champions.IChampions;
import crystal.champions.affix.Affix;
import crystal.champions.affix.AffixRegistry;
import crystal.champions.affix.SplitterAffix;
import crystal.champions.util.ChampionRank;
import crystal.champions.util.PrepareChampions;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;
import java.util.List;

/**
 * Splitter: more pieces on death; some pieces keep one of the parent's other affixes
 * (as a champion of the lowest tier with enough slots) but never drop champion loot.
 */
@Mixin(Slime.class)
public class SplitterMixin {

    @Unique
    private boolean champions$isSplitter() {
        return this instanceof IChampions champion && champion.champions$hasAffix("splitter");
    }

    // The piece count is 2 + random.nextInt(3)
    @ModifyExpressionValue(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int champions$morePieces(int original) {
        return champions$isSplitter() ? original + SplitterAffix.EXTRA_PIECES : original;
    }

    // Runs before the piece is added to the world, so it doesn't roll its own champion tier.
    // Each piece has a chance to keep one of the parent's other affixes (never Splitter, so it doesn't chain),
    // and pieces never drop champion loot.
    @WrapOperation(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean champions$addPiece(Level level, Entity piece, Operation<Boolean> original) {
        if (piece instanceof Slime cubeMob && champions$isSplitter()) {
            champions$inheritAffix(cubeMob);
        }
        return original.call(level, piece);
    }

    @Unique
    private void champions$inheritAffix(Slime cubeMob) {
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
