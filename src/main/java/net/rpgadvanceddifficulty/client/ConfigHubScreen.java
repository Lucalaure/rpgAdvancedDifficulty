package net.rpgadvanceddifficulty.client;

import crystal.champions.client.config.ChampionsModMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.rpgdifficulty.config.ModMenuIntegration;

/**
 * Mod Menu only supports one config screen per mod, so this links to both
 * the difficulty scaling config and the Champions config.
 */
@Environment(EnvType.CLIENT)
public class ConfigHubScreen extends Screen {

    private final Screen parent;

    public ConfigHubScreen(Screen parent) {
        super(Text.translatable("rpgadvanceddifficulty.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 34;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("rpgadvanceddifficulty.config.difficulty"),
                button -> this.client.setScreen(new ModMenuIntegration().getModConfigScreenFactory().create(this))).dimensions(x, y, 200, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("rpgadvanceddifficulty.config.champions"),
                button -> this.client.setScreen(new ChampionsModMenu().createConfigScreen(this))).dimensions(x, y + 24, 200, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(x, y + 60, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFF);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
