package skycat.wbshop.server;

import com.google.gson.Gson;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.FileNotFoundException;

@Environment(EnvType.SERVER)
public class WBShopServer implements DedicatedServerModInitializer, ServerLifecycleEvents.ServerStopping { // ServerLifecycleEvents.ServerStopping allows us to listen for the server stopping

    public static final EconomyManager ECONOMY_MANAGER = EconomyManager.makeNewManager();
    public static final Gson GSON = new Gson();
    // public static final Logger LOGGER = LoggerFactory.getLogger("wbshop"); // Need to fix this

    private static int donateCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        System.out.println("Donate called by " + context.getSource().getDisplayName().asString());
        SimpleNamedScreenHandlerFactory screenHandlerFactory = new SimpleNamedScreenHandlerFactory(
                (syncId, inv, player) -> {
                    // ScreenHandler handler = GenericContainerScreenHandler.createGeneric9x6(syncId, inv);
                    DonateScreenHandler handler = new DonateScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, inv, new SimpleInventory(54), 6); // 54 for 6 rows of 9
                    DonationManager.addHandler(handler);
                    return handler;
                }, // We need to somehow get this ScreenHandler outside this method call
                Text.of("MyGui")
        );
        /* OptionalInt syncId = */ context.getSource().getPlayer().openHandledScreen(screenHandlerFactory); // Create the screen handler and get the syncId
        // System.out.println(syncId.toString());

        return 1;
    }

    @Override
    public void onInitializeServer() {
        System.out.println("WBShop Initializing (Server)");
        ServerLifecycleEvents.SERVER_STOPPING.register(this);
        DonationManager.initializePointValues();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            // Props to u/profbj on reddit
            dispatcher.register(CommandManager.literal("donate").executes(WBShopServer::donateCalled));
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("pay").executes(WBShopServer::payCalled))); // Looking to standardize this method reference
    }



    private static int payCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        System.out.println(context.getSource().getPlayer().getUuid());
        return 0;
    }

    @Override
    public void onServerStopping(MinecraftServer server) {
        try {
            boolean success = ECONOMY_MANAGER.saveToFile();
            if (!success) {
                System.out.println("ERROR: Failed to save economy manager to file!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Failed to save economy manager to file! Printing stacktrace.");
            e.printStackTrace();
        }
    }
}
