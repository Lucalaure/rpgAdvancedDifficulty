package net.rpgadvanceddifficulty.client;

import crystal.champions.client.config.ChampionsModMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.rpgdifficulty.config.ModMenuIntegration;

/**
 * Mod Menu only supports one config screen per mod, so this links to both
 * the difficulty scaling config and the Champions config.
 */
@Environment(EnvType.CLIENT)
public class ConfigHubScreen extends Screen {

    private final Screen parent;

    public ConfigHubScreen(Screen parent) {
        super(Component.translatable("rpgadvanceddifficulty.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 34;

        this.addRenderableWidget(Button.builder(Component.translatable("rpgadvanceddifficulty.config.difficulty"),
                button -> this.minecraft.gui.setScreen(new ModMenuIntegration().getModConfigScreenFactory().create(this))).bounds(x, y, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("rpgadvanceddifficulty.config.champions"),
                button -> this.minecraft.gui.setScreen(new ChampionsModMenu().createConfigScreen(this))).bounds(x, y + 24, 200, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).bounds(x, y + 60, 200, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}
