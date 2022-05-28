package skycat.wbshop.server;

import com.google.gson.Gson;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@Environment(EnvType.SERVER)
public class WBShopServer implements DedicatedServerModInitializer, ServerLifecycleEvents.ServerStopping, ServerLifecycleEvents.ServerStarted { // ServerLifecycleEvents.ServerStopping allows us to listen for the server stopping

    public static final Gson GSON = new Gson();
    public static final EconomyManager ECONOMY_MANAGER = EconomyManager.makeNewManager(); // Must be after GSON declaration
    // public static final Logger LOGGER = LoggerFactory.getLogger("wbshop"); // TODO: Fix this
    public static final VoteManager VOTE_MANAGER = VoteManager.loadOrMake();
    public static MinecraftServer SERVER_INSTANCE;

    private static int donateCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        System.out.println("Donate called by " + context.getSource().getDisplayName().asString());
        SimpleNamedScreenHandlerFactory screenHandlerFactory = new SimpleNamedScreenHandlerFactory(
                (syncId, inv, player) -> {
                    // ScreenHandler handler = GenericContainerScreenHandler.createGeneric9x6(syncId, inv);
                    DonateScreenHandler handler = new DonateScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, inv, new SimpleInventory(54), 6); // 54 for 6 rows of 9
                    DonationManager.addHandler(handler);
                    return handler;
                },
                Text.of("MyGui")
        );
        /* OptionalInt syncId = */ context.getSource().getPlayer().openHandledScreen(screenHandlerFactory); // Create the screen handler and get the syncId
        return 1;
    }

    @Override
    public void onInitializeServer() {
        System.out.println("WBShop Initializing (Server)");
        ServerLifecycleEvents.SERVER_STOPPING.register(this);
        ServerLifecycleEvents.SERVER_STARTED.register(this);
        DonationManager.initializePointValues();
        registerCommands();
    }

    private void registerCommands() {
        // Props to u/profbj on reddit for showing me how to register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("donate").executes(WBShopServer::donateCalled));
            dispatcher.register(literal("pay").executes(WBShopServer::payCalled));
            dispatcher.register(literal("bal").executes(WBShopServer::balCalled));
            dispatcher.register(literal("vote")
                    .then(argument("policy", IntegerArgumentType.integer(0))
                            .then(argument("amount", IntegerArgumentType.integer(1))
                                    .executes(WBShopServer::voteCalledWithArgs)))
                    .executes(context -> {
                        // TODO: Better explanation
                        context.getSource().getPlayer().sendMessage(Text.of("Use this command to vote for policies."), false);
                        // TODO: Needs to list available policies
                        return 0;
                    })
            );
            /*
            dispatcher.register(literal("policy")
                    .then(argument("operation", StringArgumentType.word())) // TODO: Broken when used with argument. Maybe needs to be greedy if it's the last argument?
                    .executes(WBShopServer::policyCalled)
                    .executes(WBShopServer::policyCalled)
            );
             */
        });
    }

    private static int voteCalledWithArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Optimize
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UUID uuid = context.getSource().getPlayer().getUuid();
        Vote vote = new Vote(uuid, amount, LocalDateTime.now());
        VOTE_MANAGER.addVote(vote, context.getArgument("policy", int.class));
        ECONOMY_MANAGER.removeBalance(uuid, amount); // TODO: Can't detect failure yet
        context.getSource().getPlayer().sendMessage(Text.of("Success!"), false);
        System.out.println("Player " + context.getSource().getPlayer().getName().asString() + " voted for policy #" + IntegerArgumentType.getInteger(context, "policy") + " with " + IntegerArgumentType.getInteger(context, "amount") + " points."); // I'm not sure about whether "policy" has to be the same object as when we used it to register
        return 0;
    }

    private static int payCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Placeholder
        System.out.println(context.getSource().getPlayer().getUuid());
        return 0;
    }

    private static int balCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity thePlayer = context.getSource().getPlayer();
        thePlayer.sendMessage(Text.of("You have " + ECONOMY_MANAGER.getBalance(thePlayer.getUuid()) + " points."),false);
        return 0;
    }

    private static int policyCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // context.getSource().getPlayer().sendMessage(Text.of(context.getArgument("operation", String.class)), false);
        context.getSource().getPlayer().sendMessage(Text.of("hello there"), false);
        return 1;
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
