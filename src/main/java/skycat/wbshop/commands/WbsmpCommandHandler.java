package skycat.wbshop.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import skycat.wbshop.server.EconomyManager;
import skycat.wbshop.server.WorldBorderHelper;

public class WbsmpCommandHandler {
    public static int wbsmpCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Placeholder
        return 1;
    }

    public static int setPointsPerBlock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WorldBorderHelper.setPointsPerBlock(context.getArgument("points", Double.class));
        WorldBorderHelper.updateWorldBorder(EconomyManager.getInstance());
        return 1;
    }

    public static int econ(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().getPlayer().sendMessage(Text.of("/econ: Manage the economy"), false);
        return 1;
    }

    public static int econRemove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().getPlayer().sendMessage(Text.of("/econ remove: Remove points from a player"), false);
        return 1;
    }

    public static int econRemoveWithArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = context.getArgument("player", ServerPlayerEntity.class);
        int amount = context.getArgument("amount", Integer.class);
        EconomyManager.getInstance().removeBalance(target.getUuid(), amount);
        return 1;
    }
}
