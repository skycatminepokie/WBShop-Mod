package skycat.wbshop.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import skycat.wbshop.WBShopServer;

public class PayCommandHandler {
    public static int payCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Placeholder
        WBShopServer.LOGGER.info(context.getSource().getPlayer().getUuid().toString());
        return 1;
    }

    public static int payCalledWithArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        return 1;
    }
}
