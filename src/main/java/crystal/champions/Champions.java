package crystal.champions;

import crystal.champions.config.ChampionsConfigAffixes;
import crystal.champions.config.ChampionsConfigServer;
import crystal.champions.effects.CustomStatusEffects;
import crystal.champions.util.ChampionRank;
import crystal.champions.util.net.Payload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static crystal.champions.affix.AffixRegistry.affixesRegister;

public class Champions implements ModInitializer {
	public static final String MOD_ID = "champions";
	public static final Logger LOGGER = LoggerFactory.getLogger("Champions");
    public static final SimpleParticleType CHAMPIONS_SPELL = FabricParticleTypes.simple();

	@Override
	public void onInitialize() {
		LOGGER.info("Loading champions...");
        ChampionsConfigServer.get();
        ChampionsConfigAffixes.get();
        ChampionRank.get();

        Payload.register();
        affixesRegister();
        CustomStatusEffects.registerEffects();

        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "champions_spell"), CHAMPIONS_SPELL);
    }
}