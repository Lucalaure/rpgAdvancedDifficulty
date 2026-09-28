package crystal.champions.client;

import crystal.champions.Champions;
import crystal.champions.client.particle.ChampionsParticle;
import crystal.champions.client.render.ChampionHudRender;
import crystal.champions.config.ChampionsConfigClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

import static crystal.champions.client.net.ClientPacket.registerPackets;


public class ChampionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Champions.LOGGER.info("Loading champions on client...");
        ChampionsConfigClient.get();
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("champions", "champion_hud"), new ChampionHudRender() {});
        registerPackets();

        ParticleProviderRegistry.getInstance().register(Champions.CHAMPIONS_SPELL,
                spriteProvider -> (type, world, x, y, z, vx, vy, vz, random) -> {
                    final int color = (int) vx;
                    return new ChampionsParticle(world, x, y, z, 0, 0.05, 0, spriteProvider, color);
                }
        );
    }
}
