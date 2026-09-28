package crystal.champions.client.bestiary;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Book icon shown next to the recipe book button in the inventory.
 * Same 20x18 size as the recipe book button, drawn with the written book item.
 */
@Environment(EnvType.CLIENT)
public class BestiaryButton extends Button {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 18;
    private static final ItemStack ICON = new ItemStack(Items.WRITTEN_BOOK, 1);

    public BestiaryButton(int x, int y, OnPress onPress) {
        super(x, y, WIDTH, HEIGHT, Component.translatable("champions.bestiary.title"), onPress, DEFAULT_NARRATION);
        this.setTooltip(Tooltip.create(Component.translatable("champions.bestiary.title")));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (this.isHoveredOrFocused()) {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x50FFFFFF);
        }
        graphics.item(ICON, this.getX() + 2, this.getY() + 1);
    }
}
