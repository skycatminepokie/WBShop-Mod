package skycat.wbshop;

import com.google.gson.Gson;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public static final Logger LOGGER = LoggerFactory.getLogger("wbshop");
    public static final VoteManager VOTE_MANAGER = VoteManager.loadOrMake();
    public static final Settings SETTINGS = Settings.load();
    public static MinecraftServer SERVER_INSTANCE;

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

    @Override
    public void onServerStarted(MinecraftServer server) {
        SERVER_INSTANCE = server;
    }

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
        SETTINGS.save();
        // Try to save the vote manager
        // TODO: Hotfix for error saving vote manage: just don't save it xd
        // WARN: VOTE_MANAGER does not save
        /*
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
         */
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
                            .executes(VoteCommandHandler::calledWithPolicy))
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
                            .then(literal("pointLoss")
                                    .then(argument("loss", DoubleArgumentType.doubleArg(0.0))
                                            .executes(WbsmpCommandHandler::setDeathLoss)
                                    )
                            )
                            .then(literal("econ")
                                    .then(literal("remove")
                                            .then(argument("player", GameProfileArgumentType.gameProfile())
                                                    .then(argument("amount", IntegerArgumentType.integer(0))
                                                            .executes(WbsmpCommandHandler::econRemoveWithArgs)
                                                    )
                                            )
                                            .executes(WbsmpCommandHandler::econRemove)
                                    )
                                    .then(literal("add")
                                            .then(argument("player", GameProfileArgumentType.gameProfile())
                                                    .then(argument("amount", IntegerArgumentType.integer(0))
                                                            .executes(WbsmpCommandHandler::econAddWithArgs)
                                                    )
                                            )
                                            .executes(WbsmpCommandHandler::econAdd)
                                    )
                                    .then(literal("get")
                                            .then(argument("player", GameProfileArgumentType.gameProfile())
                                                    .executes(WbsmpCommandHandler::econGetWithArgs)
                                            )
                                            .executes(WbsmpCommandHandler::econGet)
                                    )
                                    .executes(WbsmpCommandHandler::econ)
                            )
            );
        });
    }
}
