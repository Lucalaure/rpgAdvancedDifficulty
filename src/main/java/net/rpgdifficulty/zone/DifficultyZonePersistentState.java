package net.rpgdifficulty.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DifficultyZonePersistentState extends SavedData {

    // Stored as data/rpgdifficulty_zones.dat in the world folder (data/rpgdifficulty/zones.dat on 26.x)
    private static final String NAME = "rpgdifficulty_zones";

    private final List<DifficultyZone> zones = new ArrayList<>();

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (DifficultyZone zone : zones) {
            list.add(zone.toNbt());
        }
        nbt.put("Zones", list);
        return nbt;
    }

    public static SavedData.Factory<DifficultyZonePersistentState> getPersistentStateType() {
        return new SavedData.Factory<>(DifficultyZonePersistentState::new, (nbt, registries) -> fromNbt(nbt), null);
    }

    public static DifficultyZonePersistentState fromNbt(CompoundTag nbt) {
        DifficultyZonePersistentState state = new DifficultyZonePersistentState();
        ListTag list = nbt.getList("Zones", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            state.zones.add(DifficultyZone.fromNbt(list.getCompound(i)));
        }
        return state;
    }

    public List<DifficultyZone> getZones() {
        return zones;
    }

    public void addZone(DifficultyZone zone) {
        zones.add(zone);
        setDirty();
    }

    public boolean removeZone(UUID id) {
        boolean removed = zones.removeIf(zone -> zone.getId().equals(id));
        if (removed) {
            setDirty();
        }
        return removed;
    }

    public Optional<DifficultyZone> findZone(String dimensionKey, double x, double y, double z) {
        for (DifficultyZone zone : zones) {
            if (zone.contains(dimensionKey, x, y, z)) {
                return Optional.of(zone);
            }
        }
        return Optional.empty();
    }

    public static DifficultyZonePersistentState get(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(getPersistentStateType(), NAME);
    }
}
