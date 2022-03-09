package skycat.wbshop.server;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

@Environment(EnvType.SERVER)
public class WBShopServer implements DedicatedServerModInitializer {

    private static int donateCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        System.out.println("Donate called by " + context.getSource().getDisplayName().asString());
        context.getSource().getPlayer().openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, inv, player) -> GenericContainerScreenHandler.createGeneric9x6(syncId, inv),
                Text.of("MyGui")
        ));
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
