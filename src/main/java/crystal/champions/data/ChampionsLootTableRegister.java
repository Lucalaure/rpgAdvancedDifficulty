package crystal.champions.data;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class ChampionsLootTableRegister implements ModInitializer {

    @Override
    public void onInitialize() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
            if (source.isBuiltin() && key.location().getPath().startsWith("entities/")) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1));
                for (int tier = 1; tier < 6; tier++) {
                    ResourceKey<LootTable> tierKey = ResourceKey.create(Registries.LOOT_TABLE,
                            ResourceLocation.fromNamespaceAndPath("champions", "champions/tier_" + tier));
                    poolBuilder.add(NestedLootTable.lootTableReference(tierKey)
                            .when(LootItemEntityPropertyCondition.hasProperties(
                                    LootContext.EntityTarget.THIS,
                                    EntityPredicate.Builder.entity()
                                            .nbt(new NbtPredicate(createTierNbt(tier)))
                            ))
                    );
                }
                tableBuilder.withPool(poolBuilder);
            }
        });
    }
    private static CompoundTag createTierNbt(int tier) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("tier", tier);
        // Champions that shouldn't drop loot (Splitter pieces) save championLoot = false
        nbt.putBoolean("championLoot", true);
        return nbt;
    }
}
