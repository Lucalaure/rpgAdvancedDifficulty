package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

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
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, config.blindDuration, 0, false, true, true));
        }
    }
}
