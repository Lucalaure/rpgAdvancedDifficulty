package crystal.champions.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.Item;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Extra loot for champions, on top of the mob's normal drops. Each tier rolls a weighted pool of useful items;
 * enchanted books are only one possible roll, and their strength scales with the tier (enchanting-table
 * levels, no treasure enchantments below tier 5).
 * Regenerate the JSON in src/main/generated with ./gradlew runDatagen.
 */
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
        // Tier 1: one roll, mostly basic materials; about 1 in 10 is a weak book
        exporter.accept(key(1), LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ContextIntProviders.exactly(1))
                .add(item(Items.IRON_INGOT, 20, 1, 3))
                .add(item(Items.GOLD_INGOT, 15, 1, 3))
                .add(item(Items.EXPERIENCE_BOTTLE, 15, 1, 3))
                .add(item(Items.EMERALD, 10, 1, 2))
                .add(item(Items.ARROW, 10, 4, 10))
                .add(book(8, 5, 10, false))));

        // Tier 2: two rolls, better materials, rare golden apple or diamond
        exporter.accept(key(2), LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ContextIntProviders.exactly(2))
                .add(item(Items.IRON_INGOT, 15, 2, 5))
                .add(item(Items.GOLD_INGOT, 12, 2, 5))
                .add(item(Items.EXPERIENCE_BOTTLE, 15, 2, 5))
                .add(item(Items.EMERALD, 12, 2, 4))
                .add(item(Items.LAPIS_LAZULI, 8, 4, 10))
                .add(item(Items.GOLDEN_APPLE, 5, 1, 1))
                .add(item(Items.DIAMOND, 4, 1, 1))
                .add(book(10, 10, 18, false))));

        // Tier 3: two rolls, valuables and mid-level books or gear
        exporter.accept(key(3), LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ContextIntProviders.exactly(2))
                .add(item(Items.EXPERIENCE_BOTTLE, 15, 4, 8))
                .add(item(Items.EMERALD, 12, 3, 6))
                .add(item(Items.DIAMOND, 10, 1, 2))
                .add(item(Items.GOLDEN_APPLE, 8, 1, 2))
                .add(item(Items.ENDER_PEARL, 8, 1, 3))
                .add(item(Items.NAME_TAG, 5, 1, 1))
                .add(enchanted(Items.BOW, 5, 15, 25, false))
                .add(book(15, 18, 25, false))));

        // Tier 4: three rolls, strong books and gear, rare netherite scrap or totem
        exporter.accept(key(4), LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ContextIntProviders.exactly(3))
                .add(item(Items.DIAMOND, 12, 2, 4))
                .add(item(Items.EMERALD, 10, 5, 10))
                .add(item(Items.EXPERIENCE_BOTTLE, 12, 6, 12))
                .add(item(Items.GOLDEN_APPLE, 8, 2, 2))
                .add(item(Items.NETHERITE_SCRAP, 5, 1, 1))
                .add(item(Items.TOTEM_OF_UNDYING, 3, 1, 1))
                .add(enchanted(Items.DIAMOND_SWORD, 4, 20, 30, false))
                .add(enchanted(Items.DIAMOND_CHESTPLATE, 4, 20, 30, false))
                .add(book(18, 25, 30, false))));

        // Tier 5: boss-level rewards; the only tier whose books can have treasure enchantments (Mending etc.)
        exporter.accept(key(5), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(Items.WITHER_SKELETON_SKULL))
                        .add(LootItem.lootTableItem(Items.NETHER_STAR)))
                .withPool(armorTrims(ContextIntProviders.between(1, 2)))
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(2))
                        .add(book(1, 30, 30, true)))
                .withPool(LootPool.lootPool()
                        .setRolls(ContextIntProviders.exactly(3))
                        .add(item(Items.DIAMOND, 12, 3, 6))
                        .add(item(Items.NETHERITE_SCRAP, 8, 1, 3))
                        .add(item(Items.GOLDEN_APPLE, 8, 2, 4))
                        .add(item(Items.TOTEM_OF_UNDYING, 5, 1, 1))
                        .add(item(Items.ENCHANTED_GOLDEN_APPLE, 2, 1, 1))));
    }

    private static ResourceKey<LootTable> key(int tier) {
        return ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("champions", "champions/tier_" + tier));
    }

    private static UniformContainerBase.Builder<?> item(Item item, int weight, int min, int max) {
        UniformContainerBase.Builder<?> entry = LootItem.lootTableItem(item).setWeight(weight);
        return min == max && min == 1 ? entry : entry.apply(SetItemCountFunction.setCount(ContextIntProviders.between(min, max)));
    }

    private UniformContainerBase.Builder<?> book(int weight, int minLevels, int maxLevels, boolean treasure) {
        return enchanted(Items.BOOK, weight, minLevels, maxLevels, treasure);
    }

    /** Enchanted like an enchanting table at the given levels. Without treasure only enchanting-table enchantments. */
    private UniformContainerBase.Builder<?> enchanted(Item item, int weight, int minLevels, int maxLevels, boolean treasure) {
        HolderGetter<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return LootItem.lootTableItem(item).setWeight(weight).apply(
                EnchantWithLevelsFunction.enchantWithLevels(enchantments, minLevels == maxLevels ? ContextIntProviders.exactly(minLevels) : ContextIntProviders.between(minLevels, maxLevels))
                        .withOptions(enchantments.getOrThrow(treasure ? EnchantmentTags.ON_RANDOM_LOOT : EnchantmentTags.IN_ENCHANTING_TABLE)));
    }

    private static LootPool.Builder armorTrims(net.minecraft.core.Holder<net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider> rolls) {
        LootPool.Builder pool = LootPool.lootPool().setRolls(rolls);
        for (Item trim : new Item[]{Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
                Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE}) {
            pool.add(LootItem.lootTableItem(trim));
        }
        return pool;
    }
}
