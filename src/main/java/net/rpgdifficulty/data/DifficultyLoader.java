package net.rpgdifficulty.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.rpgdifficulty.RpgDifficultyMain;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class DifficultyLoader implements ResourceManagerReloadListener {

    public static HashMap<String, HashMap<String, Object>> dimensionDifficulty = new HashMap<>();

    // Registered with Fabric's ResourceLoader under this id
    public static final Identifier ID = Identifier.fromNamespaceAndPath("rpgdifficulty", "difficulty_loader");

    @Override
    public void onResourceManagerReload(ResourceManager manager) {

        dimensionDifficulty.clear();
        manager.listResources("difficulty", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.open();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                HashMap<String, Object> map = new HashMap<String, Object>();
                // coordinates
                if (data.has("distanceCoordinatesX")) {
                    map.put("distanceCoordinatesX", data.get("distanceCoordinatesX").getAsInt());
                }
                if (data.has("distanceCoordinatesZ")) {
                    map.put("distanceCoordinatesZ", data.get("distanceCoordinatesZ").getAsInt());
                }
                // distance
                if (data.has("increasingDistance")) {
                    map.put("increasingDistance", data.get("increasingDistance").getAsInt());
                } else {
                    map.put("increasingDistance", RpgDifficultyMain.CONFIG.increasingDistance);
                }
                if (data.has("distanceFactor")) {
                    map.put("distanceFactor", data.get("distanceFactor").getAsDouble());
                } else {
                    map.put("distanceFactor", RpgDifficultyMain.CONFIG.distanceFactor);
                }
                // time
                if (data.has("increasingTime")) {
                    map.put("increasingTime", data.get("increasingTime").getAsInt());
                } else {
                    map.put("increasingTime", RpgDifficultyMain.CONFIG.increasingTime);
                }
                if (data.has("timeFactor")) {
                    map.put("timeFactor", data.get("timeFactor").getAsDouble());
                } else {
                    map.put("timeFactor", RpgDifficultyMain.CONFIG.timeFactor);
                }
                // max
                if (data.has("maxFactorHealth")) {
                    map.put("maxFactorHealth", data.get("maxFactorHealth").getAsDouble());
                } else {
                    map.put("maxFactorHealth", RpgDifficultyMain.CONFIG.maxFactorHealth);
                }
                if (data.has("maxFactorDamage")) {
                    map.put("maxFactorDamage", data.get("maxFactorDamage").getAsDouble());
                } else {
                    map.put("maxFactorDamage", RpgDifficultyMain.CONFIG.maxFactorDamage);
                }
                if (data.has("maxFactorProtection")) {
                    map.put("maxFactorProtection", data.get("maxFactorProtection").getAsDouble());
                } else {
                    map.put("maxFactorProtection", RpgDifficultyMain.CONFIG.maxFactorProtection);
                }
                // starting
                if (data.has("startingFactor")) {
                    map.put("startingFactor", data.get("startingFactor").getAsDouble());
                } else {
                    map.put("startingFactor", RpgDifficultyMain.CONFIG.startingFactor);
                }
                if (data.has("startingDistance")) {
                    map.put("startingDistance", data.get("startingDistance").getAsInt());
                } else {
                    map.put("startingDistance", RpgDifficultyMain.CONFIG.startingDistance);
                }
                if (data.has("startingTime")) {
                    map.put("startingTime", data.get("startingTime").getAsInt());
                } else {
                    map.put("startingTime", RpgDifficultyMain.CONFIG.startingTime);
                }

                dimensionDifficulty.put(data.get("dimension").getAsString(), map);

            } catch (Exception e) {
                RpgDifficultyMain.LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
    }

}