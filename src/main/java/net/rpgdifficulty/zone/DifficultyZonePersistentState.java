package net.rpgdifficulty.zone;

import com.mojang.serialization.Codec;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DifficultyZonePersistentState extends SavedData {

    // Serialized through the old NBT layout ("Zones" list) so the stored data format stays the same
    public static final Codec<DifficultyZonePersistentState> CODEC = CompoundTag.CODEC.xmap(DifficultyZonePersistentState::fromNbt, DifficultyZonePersistentState::writeNbt);

    // Stored as data/rpgdifficulty/zones.dat in the world folder (was data/rpgdifficulty_zones.dat in 1.21.1)
    public static final SavedDataType<DifficultyZonePersistentState> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("rpgdifficulty", "zones"), DifficultyZonePersistentState::new, CODEC, null);

    private final List<DifficultyZone> zones = new ArrayList<>();

    public CompoundTag writeNbt() {
        CompoundTag nbt = new CompoundTag();
        ListTag list = new ListTag();
        for (DifficultyZone zone : zones) {
            list.add(zone.toNbt());
        }
        nbt.put("Zones", list);
        return nbt;
    }

    public static SavedDataType<DifficultyZonePersistentState> getPersistentStateType() {
        return TYPE;
    }

    public static DifficultyZonePersistentState fromNbt(CompoundTag nbt) {
        DifficultyZonePersistentState state = new DifficultyZonePersistentState();
        ListTag list = nbt.getListOrEmpty("Zones");
        for (int i = 0; i < list.size(); i++) {
            list.getCompound(i).ifPresent(zoneNbt -> state.zones.add(DifficultyZone.fromNbt(zoneNbt)));
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
        return server.getDataStorage().computeIfAbsent(getPersistentStateType());
    }
}
