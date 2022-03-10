package skycat.wbshop.server;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

@Environment(EnvType.SERVER)
public class WBShopServer implements DedicatedServerModInitializer {

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
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            // Props to u/profbj on reddit
            dispatcher.register(CommandManager.literal("donate").executes(WBShopServer::donateCalled));
        });
    }
}
