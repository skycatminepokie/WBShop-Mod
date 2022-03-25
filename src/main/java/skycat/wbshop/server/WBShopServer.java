package skycat.wbshop.server;

import com.google.gson.Gson;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.FileNotFoundException;

import static net.minecraft.server.command.CommandManager.*;

@Environment(EnvType.SERVER)
public class WBShopServer implements DedicatedServerModInitializer, ServerLifecycleEvents.ServerStopping { // ServerLifecycleEvents.ServerStopping allows us to listen for the server stopping

    public static final Gson GSON = new Gson();
    public static final EconomyManager ECONOMY_MANAGER = EconomyManager.makeNewManager(); // Must be after GSON declaration
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
            dispatcher.register(literal("donate").executes(WBShopServer::donateCalled));
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(literal("pay").executes(WBShopServer::payCalled))); // Looking to standardize this method reference
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(literal("bal").executes(WBShopServer::balCalled)));
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> dispatcher.register(literal("vote")
                .then(argument("policy", IntegerArgumentType.integer(0))
                        .then(argument("amount", IntegerArgumentType.integer(1))
                                .executes(WBShopServer::voteCalledWithArgs)))
                .executes(context -> {
                    // TODO: Better explanation
                    context.getSource().getPlayer().sendMessage(Text.of("Use this command to vote for policies."), false);
                    return 0;
                })
        )));
    }

    private static int voteCalledWithArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Finish this
        System.out.println("This is the part where " + context.getSource().getPlayer().getName().asString() + " votes for policy #" + IntegerArgumentType.getInteger(context, "policy") + " with " + IntegerArgumentType.getInteger(context, "amount") + " points."); // I'm not sure about whether "policy" has to be the same object as when we used it to register
        return 0;
    }

    private static int payCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        System.out.println(context.getSource().getPlayer().getUuid());
        return 0;
    }

    private static int balCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity thePlayer = context.getSource().getPlayer();
        thePlayer.sendMessage(Text.of("You have " + ECONOMY_MANAGER.getBalance(thePlayer.getUuid()) + " points."),false);
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
