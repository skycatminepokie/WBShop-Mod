package skycat.wbshop.commands;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;
import skycat.wbshop.WBShopServer;
import skycat.wbshop.server.CustomItemHandler;

public class BuyCommandHandler {
    public static int buyCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException { // TODO: Better method name, only works with a string arg
        if (context.getSource().isExecutedByPlayer()) {
            String itemString = context.getArgument("itemName", StringArgumentType.string().getClass()).toString();
            if (CustomItemHandler.CUSTOM_ITEMS.containsKey(itemString)) {
                // TODO If allowed to buy
                context.getSource().getPlayer().getInventory().offerOrDrop(CustomItemHandler.CUSTOM_ITEMS.get(itemString).get());
            } else {
                throw new SimpleCommandExceptionType(new LiteralMessage("Invalid item")).create();
            }
            return 1;
        } else {
            throw new SimpleCommandExceptionType(new LiteralMessage("This command must be executed by a player")).create(); // TODO Should I be using SimpleCommandExceptionType as a builder class?
        }
    }
}
