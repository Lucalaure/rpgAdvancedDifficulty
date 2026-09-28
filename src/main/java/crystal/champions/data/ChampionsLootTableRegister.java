package crystal.champions.data;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ChampionsLootTableRegister implements ModInitializer {

    @Override
    public void onInitialize() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
            if (source.isBuiltin() && key.getValue().getPath().startsWith("entities/")) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f));
                for (int tier = 1; tier < 6; tier++) {
                    RegistryKey<LootTable> tierKey = RegistryKey.of(RegistryKeys.LOOT_TABLE,
                            Identifier.of("champions", "champions/tier_" + tier));
                    poolBuilder.with(LootTableEntry.builder(tierKey)
                            .conditionally(EntityPropertiesLootCondition.builder(
                                    LootContext.EntityTarget.THIS,
                                    EntityPredicate.Builder.create()
                                            .nbt(new NbtPredicate(createTierNbt(tier)))
                            ))
                    );
                }
                tableBuilder.pool(poolBuilder);
            }
        });
    }
    private static NbtCompound createTierNbt(int tier) {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("tier", tier);
        return nbt;
    }
}
