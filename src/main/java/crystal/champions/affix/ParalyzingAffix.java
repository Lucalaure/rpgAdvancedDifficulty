package crystal.champions.affix;

import crystal.champions.config.ChampionsConfigAffixes;
import crystal.champions.effects.CustomStatusEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

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
            target.addEffect(new MobEffectInstance(CustomStatusEffects.STUN, config.paralyzeDuration, 0));
        }
    }
}
