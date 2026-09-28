package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.util.Random;

public class BlindedAffix extends Affix {

    public BlindedAffix() {
        super("blinded");
    }

    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();
    Random rnd = new Random();

    @Override
    public void onHurt(LivingEntity champion, LivingEntity target) {
        if (rnd.nextFloat() < config.blindChance) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, config.blindDuration, 0, false, true, true));
        }
    }
}
