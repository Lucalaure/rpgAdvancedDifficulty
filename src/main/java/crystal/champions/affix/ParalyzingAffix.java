package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import crystal.champions.effects.CustomStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Random;

public class ParalyzingAffix extends Affix{

    public ParalyzingAffix() {
        super("paralyzing");
    }

    Random rnd = new Random();
    ChampionsConfigAffixes config = ChampionsConfigAffixes.get();

    @Override
    public void onHurt(LivingEntity champion, LivingEntity target) {
        if (rnd.nextFloat() < config.paralyzeChance) {
            target.addStatusEffect(new StatusEffectInstance(CustomStatusEffects.STUN, config.paralyzeDuration, 0));
        }
    }
}
