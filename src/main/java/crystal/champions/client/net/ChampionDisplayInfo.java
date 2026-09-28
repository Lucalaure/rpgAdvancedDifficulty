package crystal.champions.client.net;

import net.minecraft.text.Text;

public record ChampionDisplayInfo(
        Text name,
        int tier,
        String affixes,
        float health,
        float maxHealth,
        long lastUpdate,
        long lastUpdateClient
) {}
