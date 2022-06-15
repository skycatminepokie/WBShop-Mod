package skycat.wbshop.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import skycat.wbshop.server.WorldBorderHelper;

public class WbsmpCommandHandler {
    public static int wbsmpCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Placeholder
        return 1;
    }

    public static int setPointsPerBlock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WorldBorderHelper.setPointsPerBlock(context.getArgument("points", double.class));
        return 1;
    }
}
