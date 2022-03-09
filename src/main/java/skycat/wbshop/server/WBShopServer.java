package skycat.wbshop.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import skycat.wbshop.WBShop;

@Environment(EnvType.SERVER)
public class WBShopServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        System.out.println("WBShop Initializing (Server)");
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            // Props to u/profbj on reddit
            dispatcher.register(CommandManager.literal("donate").executes(new DonateCommand()));
        });
    }
}
