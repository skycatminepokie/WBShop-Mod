package skycat.wbshop.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import skycat.wbshop.WBShopServer;
import skycat.wbshop.server.DonateScreenHandler;
import skycat.wbshop.server.DonationManager;

import static skycat.wbshop.util.WBShopAbstracter.textOf;

/**
 * Handles all /donate commands
 */
public class DonateCommandHandler {
    public static int donateCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WBShopServer.LOGGER.info("Donate called by " + context.getSource().getDisplayName().getString());
        SimpleNamedScreenHandlerFactory screenHandlerFactory = new SimpleNamedScreenHandlerFactory(
                (syncId, inv, player) -> {
                    DonateScreenHandler handler = new DonateScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, inv, new SimpleInventory(54), 6); // 54 for 6 rows of 9
                    DonationManager.addHandler(handler); // TODO: This might not actually be doing anything...
                    return handler;
                },
                textOf("Donate")
        );
        context.getSource().getPlayer().openHandledScreen(screenHandlerFactory); // Create the screen handler and get the syncId
        return 1;
    }
}
