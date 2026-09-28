package crystal.champions.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import crystal.champions.config.ChampionsConfigAffixes;
import crystal.champions.config.ChampionsConfigClient;
import crystal.champions.config.ChampionsConfigServer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

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
                .setTitle(Text.translatable("champions.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory hud = builder.getOrCreateCategory(Text.translatable("champions.category.client"));


        var colors = entryBuilder.startSubCategory(Text.translatable("champions.hud.colors"))
                .setExpanded(true);

        final String hex_1 = "hex_tier_1";
        final String hex_2 = "hex_tier_2";
        final String hex_3 = "hex_tier_3";
        final String hex_4 = "hex_tier_4";
        final String hex_5 = "hex_tier_5";

        final String hexFormat = "#%06X";

        final String colorHex = "champions.tooltip.color_hex";

        colors.add(entryBuilder.startColorField(Text.translatable("champions.hex_tier_1"), parseHex(configC.hexTier1))
                .setDefaultValue(parseHex("#FFFF55"))
                .setSaveConsumer(colorInt -> changes.put(hex_1, String.format(hexFormat, (0xFFFFFF & colorInt))))
                .setTooltip(Text.translatable(colorHex))
                .build());

        colors.add(entryBuilder.startColorField(Text.translatable("champions.hex_tier_2"), parseHex(configC.hexTier2))
                .setDefaultValue(parseHex("#F57C2C"))
                .setSaveConsumer(colorInt -> changes.put(hex_2, String.format(hexFormat, (0xFFFFFF & colorInt))))
                .setTooltip(Text.translatable(colorHex))
                .build());

        colors.add(entryBuilder.startColorField(Text.translatable("champions.hex_tier_3"), parseHex(configC.hexTier3))
                .setDefaultValue(parseHex("#46DFFA"))
                .setSaveConsumer(colorInt -> changes.put(hex_3, String.format(hexFormat, (0xFFFFFF & colorInt))))
                .setTooltip(Text.translatable(colorHex))
                .build());

        colors.add(entryBuilder.startColorField(Text.translatable("champions.hex_tier_4"), parseHex(configC.hexTier4))
                .setDefaultValue(parseHex("#8823DB"))
                .setSaveConsumer(colorInt -> changes.put(hex_4, String.format(hexFormat, (0xFFFFFF & colorInt))))
                .setTooltip(Text.translatable(colorHex))
                .build());

        colors.add(entryBuilder.startColorField(Text.translatable("champions.hex_tier_5"), parseHex(configC.hexTier5))
                .setDefaultValue(parseHex("#F98AFF"))
                .setSaveConsumer(colorInt -> changes.put(hex_5, String.format(hexFormat, (0xFFFFFF & colorInt))))
                .setTooltip(Text.translatable(colorHex))
                .build());

        hud.addEntry(colors.build());


        var yOffsets = entryBuilder.startSubCategory(Text.translatable("champions.hud.y_offset"))
                .setExpanded(true);

        final String yOf1 = "y_offset_stars";
        final String yOf2 = "y_offset_text";
        final String yOf3 = "y_offset_bar";
        final String yOf4 = "y_offset_affixes";

        yOffsets.add(entryBuilder.startIntField(Text.translatable("champions.y_offset_stars"), configC.yOffsetStars)
                .setDefaultValue(-5)
                .setMin(-100).setMax(5000)
                .setSaveConsumer(val -> changes.put(yOf1, val))
                .setTooltip(Text.translatable("champions.tooltip.y_offset_stars"))
                .build());

        yOffsets.add(entryBuilder.startIntField(Text.translatable("champions.y_offset_text"), configC.yOffsetText)
                .setDefaultValue(7).setMin(-100).setMax(5000)
                .setSaveConsumer(val -> changes.put(yOf2, val))
                .setTooltip(Text.translatable("champions.tooltip.y_offset_text"))
                .build());

        yOffsets.add(entryBuilder.startIntField(Text.translatable("champions.y_offset_bar"), configC.yOffsetBar)
                .setDefaultValue(19).setMin(-100).setMax(5000)
                .setSaveConsumer(val -> changes.put(yOf3, val))
                .setTooltip(Text.translatable("champions.tooltip.y_offset_bar"))
                .build());

        yOffsets.add(entryBuilder.startIntField(Text.translatable("champions.y_offset_affixes"), configC.yOffsetAffixes)
                .setDefaultValue(29).setMin(-100).setMax(5000)
                .setSaveConsumer(val -> changes.put(yOf4, val))
                .setTooltip(Text.translatable("champions.tooltip.y_offset_affixes"))
                .build());

        hud.addEntry(yOffsets.build());


        var xOffsets = entryBuilder.startSubCategory(Text.translatable("champions.hud.x_offset"))
                .setExpanded(true);

        final String xOf1 = "x_offset_stars";
        final String xOf2 = "x_offset_text";
        final String xOf3 = "x_offset_bar";
        final String xOf4 = "x_offset_affixes";

        xOffsets.add(entryBuilder.startIntField(Text.translatable("champions.x_offset_stars"), configC.xOffsetStars)
                .setDefaultValue(0).setMin(-5000).setMax(5000)
                .setSaveConsumer(val -> changes.put(xOf1, val))
                .setTooltip(Text.translatable("champions.tooltip.x_offset_stars"))
                .build());

        xOffsets.add(entryBuilder.startIntField(Text.translatable("champions.x_offset_text"), configC.xOffsetText)
                .setDefaultValue(0).setMin(-5000).setMax(5000)
                .setSaveConsumer(val -> changes.put(xOf2, val))
                .setTooltip(Text.translatable("champions.tooltip.x_offset_text"))
                .build());

        xOffsets.add(entryBuilder.startIntField(Text.translatable("champions.x_offset_bar"), configC.xOffsetBar)
                .setDefaultValue(0).setMin(-5000).setMax(5000)
                .setSaveConsumer(val -> changes.put(xOf3, val))
                .setTooltip(Text.translatable("champions.tooltip.x_offset_bar"))
                .build());

        xOffsets.add(entryBuilder.startIntField(Text.translatable("champions.x_offset_affixes"), configC.xOffsetAffixes)
                .setDefaultValue(0).setMin(-5000).setMax(5000)
                .setSaveConsumer(val -> changes.put(xOf4, val))
                .setTooltip(Text.translatable("champions.tooltip.x_offset_affixes"))
                .build());

        hud.addEntry(xOffsets.build());


        var barRender = entryBuilder.startSubCategory(Text.translatable("champions.hud.bar_render"))
                .setExpanded(true);

        final String alwaysRender = "always_render";
        final String cacheC = "cache_client";
        final String cacheS = "cache_server";

        barRender.add(entryBuilder.startBooleanToggle(Text.translatable("champions.always_render"), configC.alwaysRenderBox)
                .setDefaultValue(false)
                .setSaveConsumer(val -> changes.put(alwaysRender, val))
                .setTooltip(Text.translatable("champions.tooltip.always_render"))
                .build());

        barRender.add(entryBuilder.startIntField(Text.translatable("champions.cache_client"), configC.cacheClient)
                .setDefaultValue(1000).setMin(0).setMax(10000)
                .setSaveConsumer(val -> changes.put(cacheC, val))
                .setTooltip(Text.translatable("champions.tooltip.cache_client"))
                .build());

        barRender.add(entryBuilder.startIntField(Text.translatable("champions.cache_server"), configC.cacheServer)
                .setDefaultValue(5000).setMin(0).setMax(10000)
                .setSaveConsumer(val -> changes.put(cacheS, val))
                .setTooltip(Text.translatable("champions.tooltip.cache_server"))
                .build());

        hud.addEntry(barRender.build());



        ConfigCategory server = builder.getOrCreateCategory(Text.translatable("champions.category.server"));

        var tiersWeight = entryBuilder.startSubCategory(Text.translatable("champions.hud.tiers_w"))
                .setExpanded(true);

        final String w0 = "tier0_weight";
        final String w1 = "tier1_weight";
        final String w2 = "tier2_weight";
        final String w3 = "tier3_weight";
        final String w4 = "tier4_weight";
        final String w5 = "tier5_weight";


        tiersWeight.add(entryBuilder.startIntField(Text.translatable("champions.tier0_weight"), configS.w0)
                .setDefaultValue(9460).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w0, val))
                .setTooltip(Text.translatable("champions.tooltip.tier0_weight"))
                .build());

        tiersWeight.add(entryBuilder.startIntField(Text.translatable("champions.tier1_weight"), configS.w1)
                .setDefaultValue(400).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w1, val))
                .setTooltip(Text.translatable("champions.tooltip.tier1_weight"))
                .build());

        tiersWeight.add(entryBuilder.startIntField(Text.translatable("champions.tier2_weight"), configS.w2)
                .setDefaultValue(150).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w2, val))
                .setTooltip(Text.translatable("champions.tooltip.tier2_weight"))
                .build());

        tiersWeight.add(entryBuilder.startIntField(Text.translatable("champions.tier3_weight"), configS.w3)
                .setDefaultValue(32).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w3, val))
                .setTooltip(Text.translatable("champions.tooltip.tier3_weight"))
                .build());

        tiersWeight.add(entryBuilder.startIntField(Text.translatable("champions.tier4_weight"), configS.w4)
                .setDefaultValue(6).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w4, val))
                .setTooltip(Text.translatable("champions.tooltip.tier4_weight"))
                .build());

        tiersWeight.add(entryBuilder.startIntField(Text.translatable("champions.tier5_weight"), configS.w5)
                .setDefaultValue(2).setMin(0)
                .setSaveConsumer(val -> changesServer.put(w5, val))
                .setTooltip(Text.translatable("champions.tooltip.tier5_weight"))
                .build());

        server.addEntry(tiersWeight.build());

        var tiersGh = entryBuilder.startSubCategory(Text.translatable("champions.hud.tiers_gh"))
                .setExpanded(true);

        final String gh1 = "tier1_growth_health";
        final String gh2 = "tier2_growth_health";
        final String gh3 = "tier3_growth_health";
        final String gh4 = "tier4_growth_health";
        final String gh5 = "tier5_growth_health";

        tiersGh.add(entryBuilder.startFloatField(Text.translatable("tier1_growth_health"), configS.gh1)
                .setDefaultValue(1.5F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gh1, val))
                .setTooltip(Text.translatable("champions.tooltip.tier1_growth_health"))
                .build());

        tiersGh.add(entryBuilder.startFloatField(Text.translatable("tier2_growth_health"), configS.gh2)
                .setDefaultValue(2.5F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gh2, val))
                .setTooltip(Text.translatable("champions.tooltip.tier2_growth_health"))
                .build());

        tiersGh.add(entryBuilder.startFloatField(Text.translatable("tier3_growth_health"), configS.gh3)
                .setDefaultValue(4F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gh3, val))
                .setTooltip(Text.translatable("champions.tooltip.tier3_growth_health"))
                .build());

        tiersGh.add(entryBuilder.startFloatField(Text.translatable("tier4_growth_health"), configS.gh4)
                .setDefaultValue(7F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gh4, val))
                .setTooltip(Text.translatable("champions.tooltip.tier4_growth_health"))
                .build());

        tiersGh.add(entryBuilder.startFloatField(Text.translatable("tier5_growth_health"), configS.gh5)
                .setDefaultValue(12F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gh5, val))
                .setTooltip(Text.translatable("champions.tooltip.tier5_growth_health"))
                .build());

        server.addEntry(tiersGh.build());

        var tiersGs = entryBuilder.startSubCategory(Text.translatable("champions.hud.tiers_gs"))
                .setExpanded(true);

        final String gs1 = "tier1_growth_strength";
        final String gs2 = "tier2_growth_strength";
        final String gs3 = "tier3_growth_strength";
        final String gs4 = "tier4_growth_strength";
        final String gs5 = "tier5_growth_strength";

        tiersGs.add(entryBuilder.startFloatField(Text.translatable("tier1_growth_strength"), configS.gs1)
                .setDefaultValue(1.5F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gs1, val))
                .setTooltip(Text.translatable("champions.tooltip.tier1_growth_strength"))
                .build());

        tiersGs.add(entryBuilder.startFloatField(Text.translatable("tier2_growth_strength"), configS.gs2)
                .setDefaultValue(1.8F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gs2, val))
                .setTooltip(Text.translatable("champions.tooltip.tier2_growth_strength"))
                .build());

        tiersGs.add(entryBuilder.startFloatField(Text.translatable("tier3_growth_strength"), configS.gs3)
                .setDefaultValue(2.2F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gs3, val))
                .setTooltip(Text.translatable("champions.tooltip.tier3_growth_strength"))
                .build());

        tiersGs.add(entryBuilder.startFloatField(Text.translatable("tier4_growth_strength"), configS.gs4)
                .setDefaultValue(3.5F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gs4, val))
                .setTooltip(Text.translatable("champions.tooltip.tier4_growth_strength"))
                .build());

        tiersGs.add(entryBuilder.startFloatField(Text.translatable("tier5_growth_strength"), configS.gs5)
                .setDefaultValue(5F).setMin(0)
                .setSaveConsumer(val -> changesServer.put(gs5, val))
                .setTooltip(Text.translatable("champions.tooltip.tier5_growth_strength"))
                .build());

        server.addEntry(tiersGs.build());

        var tiersA = entryBuilder.startSubCategory(Text.translatable("champions.hud.tiers_a"))
                .setExpanded(true);

        final String a1 = "tier1_affixes_count";
        final String a2 = "tier2_affixes_count";
        final String a3 = "tier3_affixes_count";
        final String a4 = "tier4_affixes_count";
        final String a5 = "tier5_affixes_count";


        tiersA.add(entryBuilder.startIntField(Text.translatable("champions.tier1_affixes_count"), configS.a1)
                .setDefaultValue(1).setMin(0).setMax(30)
                .setSaveConsumer(val -> changesServer.put(a1, val))
                .setTooltip(Text.translatable("champions.tooltip.tier1_affixes_count"))
                .build());

        tiersA.add(entryBuilder.startIntField(Text.translatable("champions.tier2_affixes_count"), configS.a2)
                .setDefaultValue(2).setMin(0).setMax(30)
                .setSaveConsumer(val -> changesServer.put(a2, val))
                .setTooltip(Text.translatable("champions.tooltip.tier2_affixes_count"))
                .build());

        tiersA.add(entryBuilder.startIntField(Text.translatable("champions.tier3_affixes_count"), configS.a3)
                .setDefaultValue(3).setMin(0).setMax(30)
                .setSaveConsumer(val -> changesServer.put(a3, val))
                .setTooltip(Text.translatable("champions.tooltip.tier3_affixes_count"))
                .build());

        tiersA.add(entryBuilder.startIntField(Text.translatable("champions.tier4_affixes_count"), configS.a4)
                .setDefaultValue(4).setMin(0).setMax(30)
                .setSaveConsumer(val -> changesServer.put(a4, val))
                .setTooltip(Text.translatable("champions.tooltip.tier4_affixes_count"))
                .build());

        tiersA.add(entryBuilder.startIntField(Text.translatable("champions.tier5_affixes_count"), configS.a5)
                .setDefaultValue(8).setMin(0).setMax(30)
                .setSaveConsumer(val -> changesServer.put(a5, val))
                .setTooltip(Text.translatable("champions.tooltip.tier5_affixes_count"))
                .build());

        server.addEntry(tiersA.build());


        ConfigCategory affixes = builder.getOrCreateCategory(Text.translatable("champions.category.affixes"));

        var registry = entryBuilder.startSubCategory(Text.translatable("champions.hud.registry"))
                .setExpanded(true);

        final String r1 = "hasty_affix";
        final String r2 = "arctic_affix";
        final String r3 = "molten_affix";
        final String r4 = "desecrating_affix";
        final String r5 = "plagued_affix";
        final String r6 = "infected_affix";
        final String r7 = "adaptive_affix";
        final String r8 = "knocking_affix";
        final String r9 = "shielding_affix";
        final String r10 = "reflective_affix";
        final String r11 = "magnetic_affix";
        final String r12 = "dampening_affix";
        final String r13 = "lively_affix";
        final String r14 = "blinded_affix";
        final String r15 = "paralyzing_affix";


        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.hasty"), configA.r1)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r1, val))
                .setTooltip(Text.translatable("champions.tooltip.hasty_affix"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.arctic"), configA.r2)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r2, val))
                .setTooltip(Text.translatable("champions.tooltip.arctic_affix"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.molten"), configA.r3)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r3, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.molten"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.desecrating"), configA.r4)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r4, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.desecrating"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.plagued"), configA.r5)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r5, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.plagued"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.infected"), configA.r6)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r6, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.infected"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.adaptive"), configA.r7)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r7, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.adaptive"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.knocking"), configA.r8)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r8, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.knocking"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.shielding"), configA.r9)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r9, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.shielding"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.reflection"), configA.r10)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r10, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.reflection"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.magnetic"), configA.r11)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r11, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.magnetic"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.dampening"), configA.r12)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r12, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.dampening"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.lively"), configA.r13)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r13, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.lively"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.blinded"), configA.r14)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r14, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.blinded"))
                .build());

        registry.add(entryBuilder.startBooleanToggle(Text.translatable("affix.paralyzing"), configA.r15)
                .setDefaultValue(true)
                .setSaveConsumer(val -> changesAffix.put(r15, val))
                .setTooltip(Text.translatable("champions.tooltip.affix.paralyzing"))
                .build());

        affixes.addEntry(registry.build());



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
