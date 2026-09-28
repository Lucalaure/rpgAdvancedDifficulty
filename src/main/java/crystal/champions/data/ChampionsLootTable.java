package crystal.champions.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ChampionsLootTable extends SimpleFabricLootTableSubProvider {
    private final HolderLookup.Provider registries;

    public ChampionsLootTable(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ENTITY);
        this.registries = registryLookup.join();
    }

    // Fabric's datagen calls generate(BiConsumer) directly; vanilla's no-arg entry point is unused
    @Override
    public void run() {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> exporter) {
        generateTier(exporter, 1, LootItem.lootTableItem(Items.BOOK)
                .apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries.lookupOrThrow(Registries.ENCHANTMENT))));
        generateTier(exporter, 2, LootItem.lootTableItem(Items.BOOK)
                .apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries.lookupOrThrow(Registries.ENCHANTMENT))));
        generateTier(exporter, 3, LootItem.lootTableItem(Items.BOOK)
                .apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries.lookupOrThrow(Registries.ENCHANTMENT))));
        generateTier(exporter, 4, LootItem.lootTableItem(Items.BOOK)
                .apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries.lookupOrThrow(Registries.ENCHANTMENT))));

        generateTier5(exporter);
    }
    private void generateTier(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> exporter, int tier, LootPoolEntryContainer.Builder<?> entry) {
        if (tier >= 5) return;
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("champions", "champions/tier_" + tier));
        exporter.accept(key, LootTable.lootTable().withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(tier))
                        .add(entry)
                )
        );
    }
    private void generateTier5(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> exporter) {
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("champions", "champions/tier_" + 5));
        exporter.accept(key, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(Items.WITHER_SKELETON_SKULL))
                        .add(LootItem.lootTableItem(Items.NETHER_STAR))
                )
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.between(3, 5))
                        .add(LootItem.lootTableItem(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .add(LootItem.lootTableItem(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE))
                )
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(4))
                        .add(LootItem.lootTableItem(Items.BOOK)
                                .apply(EnchantRandomlyFunction.randomApplicableEnchantment(this.registries.lookupOrThrow(Registries.ENCHANTMENT))))
                )
        );
    }
}
