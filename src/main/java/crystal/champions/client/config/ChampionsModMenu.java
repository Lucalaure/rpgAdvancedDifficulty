package crystal.champions.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import crystal.champions.affix.AffixRegistry;
import crystal.champions.config.ChampionsConfigAffixes;
import crystal.champions.config.ChampionsConfigClient;
import crystal.champions.config.ChampionsConfigServer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

import static crystal.champions.client.render.ChampionsColor.parseHex;

public class ChampionsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    public Screen createConfigScreen(Screen parent) {
        ChampionsConfigClient configC = ChampionsConfigClient.get();
        ChampionsConfigServer configS = ChampionsConfigServer.get();
        ChampionsConfigAffixes configA = ChampionsConfigAffixes.get();

        Map<String, Object> changes = new HashMap<>();
        Map<String, Object> changesServer = new HashMap<>();
        Map<String, Object> changesAffix = new HashMap<>();


        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("champions.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory hud = builder.getOrCreateCategory(Component.translatable("champions.category.client"));


        var colors = entryBuilder.startSubCategory(Component.translatable("champions.hud.colors"))
                .setExpanded(true);

        final String hex_1 = "hex_tier_1";
        final String hex_2 = "hex_tier_2";
        final String hex_3 = "hex_tier_3";
        final String hex_4 = "hex_tier_4";
        final String hex_5 = "hex_tier_5";

        final String hexFormat = "#%06X";

        final String colorHex = "champions.tooltip.color_hex";

        colors.add(entryBuilder.startColorField(Component.translatable("champions.hex_tier_1"), parseHex(configC.hexTier1))
                .setDefaultValue(parseHex("#FFFF55"))
                .setSaveConsumer(colorInt -> changes.put(hex_1, String.format(hexFormat, (0xFFFFFF & colorInt))))
                .setTooltip(Component.translatable(colorHex))
                .build());

        colors.add(entryBuilder.startColorField(Component.translatable("champions.hex_tier_2"), parseHex(configC.hexTier2))
                .setDefaultValue(parseHex("#F57C2C"))
                .setSaveConsumer(colorInt -> changes.put(hex_2, String.format(hexFormat, (0xFFFFFF & colorInt))))
                .setTooltip(Component.translatable(colorHex))
                .build());

        colors.add(entryBuilder.startColorField(Component.translatable("champions.hex_tier_3"), parseHex(configC.hexTier3))
                .setDefaultValue(parseHex("#46DFFA"))
                .setSaveConsumer(colorInt -> changes.put(hex_3, String.format(hexFormat, (0xFFFFFF & colorInt))))
                .setTooltip(Component.translatable(colorHex))
                .build());

        colors.add(entryBuilder.startColorField(Component.translatable("champions.hex_tier_4"), parseHex(configC.hexTier4))
                .setDefaultValue(parseHex("#8823DB"))
                .setSaveConsumer(colorInt -> changes.put(hex_4, String.format(hexFormat, (0xFFFFFF & colorInt))))
                .setTooltip(Component.translatable(colorHex))
                .build());

        colors.add(entryBuilder.startColorField(Component.translatable("champions.hex_tier_5"), parseHex(configC.hexTier5))
                .setDefaultValue(parseHex("#F98AFF"))
                .setSaveConsumer(colorInt -> changes.put(hex_5, String.format(hexFormat, (0xFFFFFF & colorInt))))
                .setTooltip(Component.translatable(colorHex))
                .build());

        hud.addEntry(colors.build());


        var yOffsets = entryBuilder.startSubCategory(Component.translatable("champions.hud.y_offset"))
                .setExpanded(true);

        final String yOf1 = "y_offset_stars";
        final String yOf2 = "y_offset_text";
        final String yOf3 = "y_offset_bar";
        final String yOf4 = "y_offset_affixes";

        yOffsets.add(entryBuilder.startIntField(Component.translatable("champions.y_offset_stars"), configC.yOffsetStars)
                .setDefaultValue(-5)
                .setMin(-100).setMax(5000)
                .setSaveConsumer(val -> changes.put(yOf1, val))
                .setTooltip(Component.translatable("champions.tooltip.y_offset_stars"))
                .build());

        yOffsets.add(entryBuilder.startIntField(Component.translatable("champions.y_offset_text"), configC.yOffsetText)
                .setDefaultValue(7).setMin(-100).setMax(5000)
                .setSaveConsumer(val -> changes.put(yOf2, val))
                .setTooltip(Component.translatable("champions.tooltip.y_offset_text"))
                .build());

        yOffsets.add(entryBuilder.startIntField(Component.translatable("champions.y_offset_bar"), configC.yOffsetBar)
                .setDefaultValue(19).setMin(-100).setMax(5000)
                .setSaveConsumer(val -> changes.put(yOf3, val))
                .setTooltip(Component.translatable("champions.tooltip.y_offset_bar"))
                .build());

        yOffsets.add(entryBuilder.startIntField(Component.translatable("champions.y_offset_affixes"), configC.yOffsetAffixes)
                .setDefaultValue(29).setMin(-100).setMax(5000)
                .setSaveConsumer(val -> changes.put(yOf4, val))
                .setTooltip(Component.translatable("champions.tooltip.y_offset_affixes"))
                .build());

        hud.addEntry(yOffsets.build());


        var xOffsets = entryBuilder.startSubCategory(Component.translatable("champions.hud.x_offset"))
                .setExpanded(true);

        final String xOf1 = "x_offset_stars";
        final String xOf2 = "x_offset_text";
        final String xOf3 = "x_offset_bar";
        final String xOf4 = "x_offset_affixes";

        xOffsets.add(entryBuilder.startIntField(Component.translatable("champions.x_offset_stars"), configC.xOffsetStars)
                .setDefaultValue(0).setMin(-5000).setMax(5000)
                .setSaveConsumer(val -> changes.put(xOf1, val))
                .setTooltip(Component.translatable("champions.tooltip.x_offset_stars"))
                .build());

        xOffsets.add(entryBuilder.startIntField(Component.translatable("champions.x_offset_text"), configC.xOffsetText)
                .setDefaultValue(0).setMin(-5000).setMax(5000)
                .setSaveConsumer(val -> changes.put(xOf2, val))
                .setTooltip(Component.translatable("champions.tooltip.x_offset_text"))
                .build());

        xOffsets.add(entryBuilder.startIntField(Component.translatable("champions.x_offset_bar"), configC.xOffsetBar)
                .setDefaultValue(0).setMin(-5000).setMax(5000)
                .setSaveConsumer(val -> changes.put(xOf3, val))
                .setTooltip(Component.translatable("champions.tooltip.x_offset_bar"))
                .build());

        xOffsets.add(entryBuilder.startIntField(Component.translatable("champions.x_offset_affixes"), configC.xOffsetAffixes)
                .setDefaultValue(0).setMin(-5000).setMax(5000)
                .setSaveConsumer(val -> changes.put(xOf4, val))
                .setTooltip(Component.translatable("champions.tooltip.x_offset_affixes"))
                .build());

        hud.addEntry(xOffsets.build());


        var barRender = entryBuilder.startSubCategory(Component.translatable("champions.hud.bar_render"))
                .setExpanded(true);

        final String alwaysRender = "always_render";
        final String cacheC = "cache_client";
        final String cacheS = "cache_server";

        barRender.add(entryBuilder.startBooleanToggle(Component.translatable("champions.always_render"), configC.alwaysRenderBox)
                .setDefaultValue(false)
                .setSaveConsumer(val -> changes.put(alwaysRender, val))
                .setTooltip(Component.translatable("champions.tooltip.always_render"))
                .build());

        barRender.add(entryBuilder.startIntField(Component.translatable("champions.cache_client"), configC.cacheClient)
                .setDefaultValue(1000).setMin(0).setMax(10000)
                .setSaveConsumer(val -> changes.put(cacheC, val))
                .setTooltip(Component.translatable("champions.tooltip.cache_client"))
                .build());

        barRender.add(entryBuilder.startIntField(Component.translatable("champions.cache_server"), configC.cacheServer)
                .setDefaultValue(5000).setMin(0).setMax(10000)
                .setSaveConsumer(val -> changes.put(cacheS, val))
                .setTooltip(Component.translatable("champions.tooltip.cache_server"))
                .build());

        hud.addEntry(barRender.build());



        ConfigCategory server = builder.getOrCreateCategory(Component.translatable("champions.category.server"));

        var tiersWeight = entryBuilder.startSubCategory(Component.translatable("champions.hud.tiers_w"))
                .setExpanded(true);

        final String w0 = "tier0_weight";
        final String w1 = "tier1_weight";
        final String w2 = "tier2_weight";
        final String w3 = "tier3_weight";
        final String w4 = "tier4_weight";
        final String w5 = "tier5_weight";


        tiersWeight.add(entryBuilder.startIntField(Component.translatable("champions.tier0_weight"), configS.w0)
                .setDefaultValue(9450).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w0, val))
                .setTooltip(Component.translatable("champions.tooltip.tier0_weight"))
                .build());

        tiersWeight.add(entryBuilder.startIntField(Component.translatable("champions.tier1_weight"), configS.w1)
                .setDefaultValue(280).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w1, val))
                .setTooltip(Component.translatable("champions.tooltip.tier1_weight"))
                .build());

        tiersWeight.add(entryBuilder.startIntField(Component.translatable("champions.tier2_weight"), configS.w2)
                .setDefaultValue(150).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w2, val))
                .setTooltip(Component.translatable("champions.tooltip.tier2_weight"))
                .build());

        tiersWeight.add(entryBuilder.startIntField(Component.translatable("champions.tier3_weight"), configS.w3)
                .setDefaultValue(80).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w3, val))
                .setTooltip(Component.translatable("champions.tooltip.tier3_weight"))
                .build());

        tiersWeight.add(entryBuilder.startIntField(Component.translatable("champions.tier4_weight"), configS.w4)
                .setDefaultValue(30).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w4, val))
                .setTooltip(Component.translatable("champions.tooltip.tier4_weight"))
                .build());

        tiersWeight.add(entryBuilder.startIntField(Component.translatable("champions.tier5_weight"), configS.w5)
                .setDefaultValue(10).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w5, val))
                .setTooltip(Component.translatable("champions.tooltip.tier5_weight"))
                .build());

        server.addEntry(tiersWeight.build());

        var tiersGh = entryBuilder.startSubCategory(Component.translatable("champions.hud.tiers_gh"))
                .setExpanded(true);

        final String gh1 = "tier1_growth_health";
        final String gh2 = "tier2_growth_health";
        final String gh3 = "tier3_growth_health";
        final String gh4 = "tier4_growth_health";
        final String gh5 = "tier5_growth_health";

        tiersGh.add(entryBuilder.startFloatField(Component.translatable("tier1_growth_health"), configS.gh1)
                .setDefaultValue(1.5F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gh1, val))
                .setTooltip(Component.translatable("champions.tooltip.tier1_growth_health"))
                .build());

        tiersGh.add(entryBuilder.startFloatField(Component.translatable("tier2_growth_health"), configS.gh2)
                .setDefaultValue(2.0F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gh2, val))
                .setTooltip(Component.translatable("champions.tooltip.tier2_growth_health"))
                .build());

        tiersGh.add(entryBuilder.startFloatField(Component.translatable("tier3_growth_health"), configS.gh3)
                .setDefaultValue(3.0F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gh3, val))
                .setTooltip(Component.translatable("champions.tooltip.tier3_growth_health"))
                .build());

        tiersGh.add(entryBuilder.startFloatField(Component.translatable("tier4_growth_health"), configS.gh4)
                .setDefaultValue(4.5F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gh4, val))
                .setTooltip(Component.translatable("champions.tooltip.tier4_growth_health"))
                .build());

        tiersGh.add(entryBuilder.startFloatField(Component.translatable("tier5_growth_health"), configS.gh5)
                .setDefaultValue(6.0F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gh5, val))
                .setTooltip(Component.translatable("champions.tooltip.tier5_growth_health"))
                .build());

        server.addEntry(tiersGh.build());

        var tiersGs = entryBuilder.startSubCategory(Component.translatable("champions.hud.tiers_gs"))
                .setExpanded(true);

        final String gs1 = "tier1_growth_strength";
        final String gs2 = "tier2_growth_strength";
        final String gs3 = "tier3_growth_strength";
        final String gs4 = "tier4_growth_strength";
        final String gs5 = "tier5_growth_strength";

        tiersGs.add(entryBuilder.startFloatField(Component.translatable("tier1_growth_strength"), configS.gs1)
                .setDefaultValue(1.25F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gs1, val))
                .setTooltip(Component.translatable("champions.tooltip.tier1_growth_strength"))
                .build());

        tiersGs.add(entryBuilder.startFloatField(Component.translatable("tier2_growth_strength"), configS.gs2)
                .setDefaultValue(1.4F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gs2, val))
                .setTooltip(Component.translatable("champions.tooltip.tier2_growth_strength"))
                .build());

        tiersGs.add(entryBuilder.startFloatField(Component.translatable("tier3_growth_strength"), configS.gs3)
                .setDefaultValue(1.6F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gs3, val))
                .setTooltip(Component.translatable("champions.tooltip.tier3_growth_strength"))
                .build());

        tiersGs.add(entryBuilder.startFloatField(Component.translatable("tier4_growth_strength"), configS.gs4)
                .setDefaultValue(1.8F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gs4, val))
                .setTooltip(Component.translatable("champions.tooltip.tier4_growth_strength"))
                .build());

        tiersGs.add(entryBuilder.startFloatField(Component.translatable("tier5_growth_strength"), configS.gs5)
                .setDefaultValue(2.0F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gs5, val))
                .setTooltip(Component.translatable("champions.tooltip.tier5_growth_strength"))
                .build());

        server.addEntry(tiersGs.build());

        var tiersA = entryBuilder.startSubCategory(Component.translatable("champions.hud.tiers_a"))
                .setExpanded(true);

        final String a1 = "tier1_affix_slots";
        final String a2 = "tier2_affix_slots";
        final String a3 = "tier3_affix_slots";
        final String a4 = "tier4_affix_slots";
        final String a5 = "tier5_affix_slots";


        tiersA.add(entryBuilder.startIntField(Component.translatable("champions.tier1_affix_slots"), configS.a1)
                .setDefaultValue(1).setMin(0).setMax(30)
                .setSaveConsumer(val -> changesServer.put(a1, val))
                .setTooltip(Component.translatable("champions.tooltip.tier1_affix_slots"))
                .build());

        tiersA.add(entryBuilder.startIntField(Component.translatable("champions.tier2_affix_slots"), configS.a2)
                .setDefaultValue(2).setMin(0).setMax(30)
                .setSaveConsumer(val -> changesServer.put(a2, val))
                .setTooltip(Component.translatable("champions.tooltip.tier2_affix_slots"))
                .build());

        tiersA.add(entryBuilder.startIntField(Component.translatable("champions.tier3_affix_slots"), configS.a3)
                .setDefaultValue(3).setMin(0).setMax(30)
                .setSaveConsumer(val -> changesServer.put(a3, val))
                .setTooltip(Component.translatable("champions.tooltip.tier3_affix_slots"))
                .build());

        tiersA.add(entryBuilder.startIntField(Component.translatable("champions.tier4_affix_slots"), configS.a4)
                .setDefaultValue(4).setMin(0).setMax(30)
                .setSaveConsumer(val -> changesServer.put(a4, val))
                .setTooltip(Component.translatable("champions.tooltip.tier4_affix_slots"))
                .build());

        tiersA.add(entryBuilder.startIntField(Component.translatable("champions.tier5_affix_slots"), configS.a5)
                .setDefaultValue(8).setMin(0).setMax(30)
                .setSaveConsumer(val -> changesServer.put(a5, val))
                .setTooltip(Component.translatable("champions.tooltip.tier5_affix_slots"))
                .build());

        server.addEntry(tiersA.build());


        ConfigCategory affixes = builder.getOrCreateCategory(Component.translatable("champions.category.affixes"));

        var registry = entryBuilder.startSubCategory(Component.translatable("champions.hud.registry"))
                .setExpanded(true);

        // One toggle per affix, generated from the registry
        for (String name : AffixRegistry.NAMES) {
            final String key = ChampionsConfigAffixes.toggleKey(name);
            registry.add(entryBuilder.startBooleanToggle(Component.translatable("affix." + name), configA.isEnabled(name))
                    .setDefaultValue(true)
                    .setSaveConsumer(val -> changesAffix.put(key, val))
                    .setTooltip(Component.translatable("affix." + name + ".desc"))
                    .build());
        }

        affixes.addEntry(registry.build());

        var affixTiers = entryBuilder.startSubCategory(Component.translatable("champions.hud.affix_slots"))
                .setExpanded(false);
        affixTiers.add(entryBuilder.startFloatField(Component.translatable("champions.affix_slot_weight_bonus"), configA.affixSlotWeightBonus)
                .setDefaultValue(0.25f).setMin(0.0f).setMax(10.0f)
                .setSaveConsumer(val -> changesAffix.put("affix_slot_weight_bonus", val))
                .setTooltip(Component.translatable("champions.tooltip.affix_slot_weight_bonus"))
                .build());
        for (String name : AffixRegistry.NAMES) {
            final String key = name + "_slots";
            affixTiers.add(entryBuilder.startIntField(Component.translatable("affix." + name), configA.slots(name))
                    .setDefaultValue(AffixRegistry.defaultSlots(name)).setMin(1).setMax(8)
                    .setSaveConsumer(val -> changesAffix.put(key, val))
                    .setTooltip(Component.translatable("champions.tooltip.affix_slots"))
                    .build());
        }
        affixes.addEntry(affixTiers.build());

        var big = entryBuilder.startSubCategory(Component.translatable("affix.big"))
                .setExpanded(true);

        big.add(entryBuilder.startIntField(Component.translatable("champions.big_bonus_health"), configA.bigBonusHealth)
                .setDefaultValue(10).setMin(0).setMax(1000)
                .setSaveConsumer(val -> changesAffix.put("big_bonus_health", val))
                .build());
        big.add(entryBuilder.startIntField(Component.translatable("champions.big_bonus_damage"), configA.bigBonusDamage)
                .setDefaultValue(2).setMin(0).setMax(100)
                .setSaveConsumer(val -> changesAffix.put("big_bonus_damage", val))
                .build());
        big.add(entryBuilder.startFloatField(Component.translatable("champions.big_slowness"), configA.bigSlowness)
                .setDefaultValue(0.7f).setMin(0.1f).setMax(2.0f)
                .setSaveConsumer(val -> changesAffix.put("big_slowness", val))
                .build());
        big.add(entryBuilder.startFloatField(Component.translatable("champions.big_size"), configA.bigSize)
                .setDefaultValue(1.3f).setMin(0.5f).setMax(3.0f)
                .setSaveConsumer(val -> changesAffix.put("big_size", val))
                .build());

        affixes.addEntry(big.build());



        builder.setSavingRunnable(() -> {
            ChampionsConfigClient.save(changes);
            ChampionsConfigServer.save(changesServer);
            ChampionsConfigAffixes.save(changesAffix);

            ChampionsConfigClient.reload();
            ChampionsConfigServer.reload();
            ChampionsConfigAffixes.reload();
        });
        return builder.build();
    }
}
