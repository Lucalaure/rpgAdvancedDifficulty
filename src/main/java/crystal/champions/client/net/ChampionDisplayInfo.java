package crystal.champions.client.net;

import net.minecraft.network.chat.Component;

public record ChampionDisplayInfo(
        Component name,
        int tier,
        String affixes,
        float health,
        float maxHealth,
        long lastUpdate,
        long lastUpdateClient
) {}
