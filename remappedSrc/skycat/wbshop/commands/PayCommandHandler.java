package skycat.wbshop.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public class PayCommandHandler {
    public static int payCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Placeholder
        System.out.println(context.getSource().getPlayer().getUuid());
        return 1;
    }

    public static int payCalledWithArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        return 1;
    }
}
