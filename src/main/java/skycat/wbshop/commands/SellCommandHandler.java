package skycat.wbshop.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import skycat.wbshop.WBShopServer;
import skycat.wbshop.server.DonateScreenHandler;
import skycat.wbshop.server.DonationManager;
import skycat.wbshop.server.SellScreenHandler;

public class SellCommandHandler {
    public static int sell(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WBShopServer.LOGGER.info("Sell called by " + context.getSource().getDisplayName().getString());
        SimpleNamedScreenHandlerFactory screenHandlerFactory = new SimpleNamedScreenHandlerFactory(
                (syncId, inv, player) -> {
                    SellScreenHandler handler = new SellScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, inv, new SimpleInventory(54), 6); // 54 for 6 rows of 9
                    return handler;
                },
                Text.of("Sell")
        );
        context.getSource().getPlayer().openHandledScreen(screenHandlerFactory); // Create the screen handler and get the syncId
        return 1;
    }
}
