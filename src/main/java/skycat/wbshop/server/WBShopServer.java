package skycat.wbshop.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import skycat.wbshop.WBShop;

@Environment(EnvType.SERVER)
public class WBShopServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        System.out.println("WBShop Initializing (server)");
        CommandRegistrationCallback.EVENT.register(DonateCommand::register);
    }
}
