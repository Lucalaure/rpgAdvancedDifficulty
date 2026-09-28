package crystal.champions.client.bestiary;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

/**
 * Adds the bestiary button to the right of the recipe book button in the survival inventory.
 * The recipe book button moves when the recipe book opens, so the bestiary button follows it every frame.
 */
@Environment(EnvType.CLIENT)
public final class BestiaryButtons {
    private BestiaryButtons() {
    }

    private static final int GAP = 2;

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof InventoryScreen)) return;

            ImageButton recipeButton = findRecipeButton(screen);
            if (recipeButton == null) return;

            BestiaryButton bestiaryButton = new BestiaryButton(recipeButton.getX() + recipeButton.getWidth() + GAP, recipeButton.getY(),
                    button -> {
                        if (client.player != null) {
                            client.gui.setScreen(new BestiaryScreen(screen, client.player));
                        }
                    });
            Screens.getWidgets(screen).add(bestiaryButton);

            ScreenEvents.beforeExtract(screen).register((s, graphics, mouseX, mouseY, tickProgress) ->
                    bestiaryButton.setPosition(recipeButton.getX() + recipeButton.getWidth() + GAP, recipeButton.getY()));
        });
    }

    private static ImageButton findRecipeButton(net.minecraft.client.gui.screens.Screen screen) {
        for (AbstractWidget widget : Screens.getWidgets(screen)) {
            if (widget instanceof ImageButton imageButton) {
                return imageButton;
            }
        }
        return null;
    }
}
