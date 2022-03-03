package skycat.wbshop;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import skycat.wbshop.server.DonateCommand;

// Do I need this?
public class WBShop implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("WBShop Initializing");
        CommandRegistrationCallback.EVENT.register(DonateCommand::register);
    }
}
