package skycat.wbshop;

import com.google.gson.Gson;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import skycat.wbshop.commands.*;
import skycat.wbshop.server.DonationManager;
import skycat.wbshop.server.EconomyManager;
import skycat.wbshop.server.VoteManager;
import skycat.wbshop.server.WorldBorderHelper;

import java.io.FileNotFoundException;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@Environment(EnvType.SERVER)
public class WBShopServer implements DedicatedServerModInitializer, ServerLifecycleEvents.ServerStopping, ServerLifecycleEvents.ServerStarted { // ServerLifecycleEvents.ServerStopping allows us to listen for the server stopping

    public static final Gson GSON = new Gson();
    public static final EconomyManager ECONOMY_MANAGER = EconomyManager.makeNewManager(); // Must be after GSON declaration
    // public static final Logger LOGGER = LoggerFactory.getLogger("wbshop"); // TODO: Fix this
    public static final VoteManager VOTE_MANAGER = VoteManager.loadOrMake();
    public static MinecraftServer SERVER_INSTANCE;
    public static final Settings SETTINGS = Settings.load();

    @Override
    public void onInitializeServer() {
        System.out.println("WBShop Initializing (Server)");
        ServerLifecycleEvents.SERVER_STOPPING.register(this);
        ServerLifecycleEvents.SERVER_STARTED.register(this);
        DonationManager.initializePointValues();
        registerCommands();
        WorldBorderHelper.setPointsPerBlock(SETTINGS.pointsPerBlock);
        WorldBorderHelper.updateWorldBorder(ECONOMY_MANAGER);
    }

    private void registerCommands() {
        // Props to u/profbj on reddit for showing me how to register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, environment) -> {
            dispatcher.register(literal("donate").executes(DonateCommandHandler::donateCalled));
            dispatcher.register(literal("pay").executes(PayCommandHandler::payCalled));
            dispatcher.register(literal("bal").executes(BalCommandHandler::balCalled));
            dispatcher.register(literal("vote")
                    .then(argument("policy", IntegerArgumentType.integer(0))
                            .then(argument("amount", IntegerArgumentType.integer(1))
                                    .executes(VoteCommandHandler::calledWithAmount))
                            .executes(VoteCommandHandler::calledWithPolicy)) // TODO: Test nesting
                    .executes(VoteCommandHandler::voteCalled)
            );
            dispatcher.register(
                    literal("wbsmp")
                            .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                                .then(literal("pointsPerBlock")
                                        .then(argument("points", DoubleArgumentType.doubleArg(0.0))
                                                .executes(WbsmpCommandHandler::setPointsPerBlock)
                                        )
                                        // Doesn't accept pointsPerBlock with no "points" argument
                                )
                                .then(literal("econ")
                                        .then(literal("remove")
                                                .then(argument("player", EntityArgumentType.player())
                                                        .then(argument("amount", IntegerArgumentType.integer(0))
                                                                .executes(WbsmpCommandHandler::econRemoveWithArgs)
                                                        )
                                                )
                                                .executes(WbsmpCommandHandler::econRemove)
                                        )
                                        .executes(WbsmpCommandHandler::econ)
                                )
            );
        });
    }

    /* private static int policyCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // context.getSource().getPlayer().sendMessage(Text.of(context.getArgument("operation", String.class)), false);
        context.getSource().getPlayer().sendMessage(Text.of("hello there"), false);
        return 1;
    }
      */
    @Override
    public void onServerStopping(MinecraftServer server) {
        // Try to save the economy manager
        try {
            boolean success = ECONOMY_MANAGER.saveToFile();
            if (!success) {
                System.out.println("ERROR: Failed to save economy manager to file!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Failed to save economy manager to file! Printing stacktrace.");
            e.printStackTrace();
            // TODO: Maybe print out alt version of econ manager so progress isn't lost?
        }
        // Try to save the vote manager
        try {
            boolean success = VOTE_MANAGER.saveToFile();
            if (!success) {
                System.out.println("ERROR: Failed to save vote manager to file!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Failed to save vote manager to file! Printing stacktrace.");
            e.printStackTrace();
            // TODO: Maybe print out alt version of vote manager so progress isn't lost?
        }
    }

    @Override
    public void onServerStarted(MinecraftServer server) {
        SERVER_INSTANCE = server;
    }
}
