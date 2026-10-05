package crystal.champions.bestiary;

import crystal.champions.Champions;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * The bestiary book. Using it opens the bestiary screen. Discoveries are stored on the player,
 * not the book, so they keep updating without it and any copy shows the same entries.
 */
public class BestiaryItem extends Item {
    public static final ResourceKey<Item> KEY = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Champions.MOD_ID, "bestiary"));
    public static Item BESTIARY;

    /** Set by the client to open the screen (keeps client classes out of common code). */
    public static Consumer<Player> clientOpener = player -> {
    };

    public BestiaryItem(Properties properties) {
        super(properties);
    }

    public static void register() {
        BESTIARY = Registry.register(BuiltInRegistries.ITEM, KEY, new BestiaryItem(new Item.Properties().setId(KEY).stacksTo(1)));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> output.insertAfter(Items.WRITABLE_BOOK, BESTIARY));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            clientOpener.accept(player);
        }
        return InteractionResult.SUCCESS;
    }

    /** Gives the player a bestiary, dropping it at their feet if their inventory is full. */
    public static void give(ServerPlayer player) {
        ItemStack book = new ItemStack(BESTIARY);
        if (!player.getInventory().add(book) && player.level() instanceof ServerLevel level) {
            player.spawnAtLocation(level, book);
        }
    }
}
