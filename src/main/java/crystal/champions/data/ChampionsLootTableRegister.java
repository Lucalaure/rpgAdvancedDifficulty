package crystal.champions.data;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.predicates.NbtPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public class ChampionsLootTableRegister implements ModInitializer {

    @Override
    public void onInitialize() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
            if (source.isBuiltin() && key.identifier().getPath().startsWith("entities/")) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1));
                for (int tier = 1; tier < 6; tier++) {
                    ResourceKey<LootTable> tierKey = ResourceKey.create(Registries.LOOT_TABLE,
                            Identifier.fromNamespaceAndPath("champions", "champions/tier_" + tier));
                    // 26.3: nested loot table entries take a Holder; Fabric's lookup here can resolve loot table references
                    poolBuilder.add(NestedLootTable.lootTableReference(wrapperLookup.getOrThrow(tierKey))
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
