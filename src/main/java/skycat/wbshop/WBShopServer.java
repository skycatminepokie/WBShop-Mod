package skycat.wbshop;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import skycat.wbshop.commands.*;
import skycat.wbshop.server.*;

import java.io.FileNotFoundException;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@Environment(EnvType.SERVER)
public class WBShopServer implements DedicatedServerModInitializer, ServerLifecycleEvents.ServerStopping, ServerLifecycleEvents.ServerStarted { // ServerLifecycleEvents.ServerStopping allows us to listen for the server stopping
    public static final Gson GSON = new GsonBuilder().create(); // TODO Work on allowing prettyprint
    public static final Logger LOGGER = LoggerFactory.getLogger("wbshop");
    public static final Settings SETTINGS = Settings.load();
    public static final EconomyManager ECONOMY_MANAGER = EconomyManager.makeNewManager(); // Must be after GSON declaration
    // public static final VoteManager VOTE_MANAGER = VoteManager.loadOrMake();
    public static final CustomItemHandler CUSTOM_ITEM_HANDLER = new CustomItemHandler();
    public static MinecraftServer SERVER_INSTANCE;

    @Override
    public void onInitializeServer() {
        WBShopServer.LOGGER.info("WBShop Initializing (Server)");
        ServerLifecycleEvents.SERVER_STOPPING.register(this);
        ServerLifecycleEvents.SERVER_STARTED.register(this);
        UseItemCallback.EVENT.register(CUSTOM_ITEM_HANDLER);
        DonationManager.reloadPointValues();
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
                WBShopServer.LOGGER.error("Failed to save economy manager to file!");
            }
        } catch (FileNotFoundException e) {
            WBShopServer.LOGGER.error("Failed to save economy manager to file! Printing stacktrace.");
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
            dispatcher.register(literal("pay")
                    .then(argument("player", GameProfileArgumentType.gameProfile())
                            .then(argument("amount", IntegerArgumentType.integer(1))
                                    .executes(PayCommandHandler::payCalledWithArgs)
                            )
                    )
                    .executes(PayCommandHandler::payCalled));
            dispatcher.register(literal("bal").executes(BalCommandHandler::balCalled));
            /*dispatcher.register(literal("vote")
                    .then(argument("policy", IntegerArgumentType.integer(0))
                            .then(argument("amount", IntegerArgumentType.integer(1))
                                    .executes(VoteCommandHandler::calledWithAmount))
                            .executes(VoteCommandHandler::calledWithPolicy))
                    .executes(VoteCommandHandler::voteCalled)
            );*/
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
                                                            .then(literal("noUpdate")
                                                                    .executes(context -> WbsmpCommandHandler.removePoints(context, false))
                                                            )
                                                            .executes(context -> WbsmpCommandHandler.removePoints(context, true))
                                                    )
                                            )
                                            .executes(WbsmpCommandHandler::econRemove)
                                    )
                                    .then(literal("add")
                                            .then(argument("player", GameProfileArgumentType.gameProfile())
                                                    .then(argument("amount", IntegerArgumentType.integer(0))
                                                            .then(literal("noUpdate")
                                                                    .executes(context -> WbsmpCommandHandler.addPoints(context, false))
                                                            )
                                                            .executes(context -> WbsmpCommandHandler.addPoints(context, true))
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
            dispatcher.register(literal("withdraw")
                    .then(argument("amount", IntegerArgumentType.integer(1))
                            .executes(WithdrawCommandHandler::withdrawCalledAmount)
                    )
                    .then(literal("all")
                            .executes(WithdrawCommandHandler::withdrawCalledAll)
                    )
            );
            dispatcher.register(literal("offer")
                    .then(literal("list")
                            .executes(OfferCommandHandler::list)
                    )
                    .then(literal("create")
                            .then(argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess))
                                    .then(argument("pointsPerItem", DoubleArgumentType.doubleArg(0))
                                            .then(argument("itemCount", IntegerArgumentType.integer(1))
                                                    .executes(context -> OfferCommandHandler.createWithArgs(
                                                                    context.getSource(),
                                                                    ItemStackArgumentType.getItemStackArgument(context, "item").getItem(),
                                                                    DoubleArgumentType.getDouble(context, "pointsPerItem"),
                                                                    IntegerArgumentType.getInteger(context, "itemCount")
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
                    .then(literal("claim")
                            .executes(OfferCommandHandler::claim)
                    )
            );
            dispatcher.register(literal("sell")
                    .executes(SellCommandHandler::sell)
            );
        });
    }
}
