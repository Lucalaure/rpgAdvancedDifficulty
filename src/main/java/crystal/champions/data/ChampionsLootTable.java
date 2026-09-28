package crystal.champions.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ChampionsLootTable extends SimpleFabricLootTableProvider {
    private final RegistryWrapper.WrapperLookup registries;

    public ChampionsLootTable(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup, LootContextTypes.ENTITY);
        this.registries = registryLookup.join();
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> exporter) {
        generateTier(exporter, 1, ItemEntry.builder(Items.BOOK)
                .apply(EnchantRandomlyLootFunction.builder(this.registries)));
        generateTier(exporter, 2, ItemEntry.builder(Items.BOOK)
                .apply(EnchantRandomlyLootFunction.builder(this.registries)));
        generateTier(exporter, 3, ItemEntry.builder(Items.BOOK)
                .apply(EnchantRandomlyLootFunction.builder(this.registries)));
        generateTier(exporter, 4, ItemEntry.builder(Items.BOOK)
                .apply(EnchantRandomlyLootFunction.builder(this.registries)));

        generateTier5(exporter);
    }
    private void generateTier(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> exporter, int tier, LootPoolEntry.Builder<?> entry) {
        if (tier >= 5) return;
        RegistryKey<LootTable> key = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("champions", "champions/tier_" + tier));
        exporter.accept(key, LootTable.builder().pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(tier))
                        .with(entry)
                )
        );
    }
    private void generateTier5(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> exporter) {
        RegistryKey<LootTable> key = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("champions", "champions/tier_" + 5));
        exporter.accept(key, LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(Items.WITHER_SKELETON_SKULL))
                        .with(ItemEntry.builder(Items.NETHER_STAR))
                )
                .pool(LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(3, 5))
                        .with(ItemEntry.builder(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE))
                        .with(ItemEntry.builder(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE))
                )
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(4))
                        .with(ItemEntry.builder(Items.BOOK)
                                .apply(EnchantRandomlyLootFunction.builder((this.registries))))
                )
        );
    }
}
