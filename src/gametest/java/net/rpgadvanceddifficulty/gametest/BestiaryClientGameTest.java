package net.rpgadvanceddifficulty.gametest;

import crystal.champions.IChampions;
import crystal.champions.affix.BigAffix;
import crystal.champions.client.bestiary.BestiaryButton;
import crystal.champions.client.bestiary.BestiaryScreen;
import net.rpgadvanceddifficulty.DifficultyReport;
import net.rpgadvanceddifficulty.client.DifficultyDebugEntry;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.zombie.Zombie;

/**
 * Spawns a champion zombie next to the player, then screenshots the champion HUD,
 * the bestiary button in the inventory (recipe book closed and open) and the bestiary pages.
 * Screenshots are written to build/run/clientGameTest/screenshots.
 */
public class BestiaryClientGameTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder()
                .adjustSettings(settings -> settings.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL))
                .create()) {
            singleplayer.getConnection().waitForChunksRender();

            singleplayer.getServer().runOnServer(server -> {
                ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
                Zombie zombie = EntityTypes.ZOMBIE.create(player.level(), EntitySpawnReason.COMMAND);
                zombie.setNoAi(true);
                zombie.setPos(player.getX() + 3, player.getY(), player.getZ());
                zombie.setYRot(90);
                player.level().addFreshEntity(zombie);
                ((IChampions) zombie).champions$setChampionTier(2);
                ((IChampions) zombie).champions$setAffixesString("big,hasty");
                new BigAffix().onApply(zombie);
            });
            context.getInput().lookAt(-90, 10);
            context.waitTicks(40);
            context.takeScreenshot("bestiary_1_champion_hud");

            context.setScreen(() -> new InventoryScreen(Minecraft.getInstance().player));
            context.waitTicks(2);
            context.takeScreenshot("bestiary_2_inventory");

            context.runOnClient(client -> press(findWidget(ImageButton.class)));
            context.waitTicks(2);
            context.takeScreenshot("bestiary_3_inventory_recipe_book_open");

            context.runOnClient(client -> press(findWidget(BestiaryButton.class)));
            context.waitForScreen(BestiaryScreen.class);
            context.takeScreenshot("bestiary_4_intro_page");

            showPage(context, 1, "bestiary_5_tiers_page");
            showPage(context, 2, "bestiary_6_odds_page");
            showPage(context, 3, "bestiary_7_loot_page");
            showPage(context, 4, "bestiary_8_difficulty_page");
            showPage(context, 5, "bestiary_9_affix_slots_page");
            // Affix pages start at 6, sorted by slots: hasty, knocking, dampening, lively, blinded, big, ...
            showPage(context, 6, "bestiary_10_hasty_page");
            showPage(context, 11, "bestiary_11_big_page");
            showPage(context, 7, "bestiary_12_undiscovered_page");

            // F3 readout: skip 5 hours of world time (5 steps of +10% on Normal) and check the synced value
            context.setScreen(() -> null);
            singleplayer.getServer().runCommand("rpgdifficulty time set 5");
            context.waitTicks(45);
            float serverFactor = singleplayer.getServer().computeOnServer(server -> {
                var player = server.getPlayerList().getPlayers().getFirst();
                return DifficultyReport.compute(player.level(), player.position()).factor();
            });
            if (Math.abs(serverFactor - 1.5f) > 0.001f) {
                throw new AssertionError("F3 readout: expected 1.5x after 5 hours on Normal, got " + serverFactor);
            }
            float clientFactor = context.computeOnClient(client -> DifficultyDebugEntry.latest() == null ? -1f : DifficultyDebugEntry.latest().factor());
            if (Math.abs(clientFactor - serverFactor) > 0.001f) {
                throw new AssertionError("F3 readout: client shows " + clientFactor + " but the server has " + serverFactor);
            }
            boolean shownByDefault = context.computeOnClient(client -> {
                client.debugEntries.setOverlayVisible(true);
                return client.debugEntries.isCurrentlyEnabled(DifficultyDebugEntry.ID);
            });
            if (!shownByDefault) {
                throw new AssertionError("F3 readout: the RPG Difficulty entry is not shown on the F3 screen by default");
            }
            context.waitTicks(2);
            context.takeScreenshot("bestiary_13_f3_difficulty");
            context.runOnClient(client -> client.debugEntries.setOverlayVisible(false));
        }
    }

    private static void showPage(ClientGameTestContext context, int page, String screenshot) {
        context.runOnClient(client -> ((BestiaryScreen) client.gui.screen()).setPage(page));
        context.waitTick();
        context.takeScreenshot(screenshot);
    }

    private static <T extends AbstractWidget> T findWidget(Class<T> type) {
        for (AbstractWidget widget : Screens.getWidgets(Minecraft.getInstance().gui.screen())) {
            if (type.isInstance(widget)) {
                return type.cast(widget);
            }
        }
        throw new AssertionError("No " + type.getSimpleName() + " on screen");
    }

    private static void press(Button button) {
        button.onPress(new KeyEvent(257, 0, 0));
    }
}
