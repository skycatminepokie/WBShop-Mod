package skycat.wbshop.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import skycat.wbshop.server.EconomyManager;
import skycat.wbshop.server.WorldBorderHelper;

import java.util.Collection;

public class WbsmpCommandHandler {
    public static int econ(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().getPlayer().sendMessage(Text.of("/econ: Manage the economy"), false);
        return 1;
    }

    public static int econAdd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().getPlayer().sendMessage(Text.of("/econ add: Add points to a player's wallet"), false);
        return 1;
    }

    public static int econAddWithArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<GameProfile> targets = GameProfileArgumentType.getProfileArgument(context, "player");
        for (GameProfile target : targets) {
            int amount = context.getArgument("amount", Integer.class);
            EconomyManager.getInstance().addBalance(target.getId(), amount);
            context.getSource().getPlayer().sendMessage(Text.of("Added " + amount + " points to " + target.getName()), false);
        }
        return 1;
    }

    public static int econGet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().getPlayer().sendMessage(Text.of("/econ get: Get the number of points a player has"), false);
        return 1;
    }

    public static int econGetWithArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<GameProfile> targets = GameProfileArgumentType.getProfileArgument(context, "player");
        for (GameProfile target : targets) {
            System.out.println("id: " + target.getId());
            context.getSource().getPlayer().sendMessage(Text.of(target.getName() + " has " + EconomyManager.getInstance().getBalance(target.getId()) + " points."), false);
        }
        return 1;
    }

    public static int econRemove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().getPlayer().sendMessage(Text.of("/econ remove: Remove points from a player's wallet"), false);
        return 1;
    }

    public static int econRemoveWithArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<GameProfile> targets = GameProfileArgumentType.getProfileArgument(context, "player");
        for (GameProfile target : targets) {
            int amount = context.getArgument("amount", Integer.class);
            EconomyManager.getInstance().removeBalance(target.getId(), amount);
            context.getSource().getPlayer().sendMessage(Text.of("Removed " + amount + " points from " + target.getName()));
        }
        return 1;
    }

    public static int setPointsPerBlock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WorldBorderHelper.setPointsPerBlock(context.getArgument("points", Double.class));
        WorldBorderHelper.updateWorldBorder(EconomyManager.getInstance());
        return 1;
    }

    public static int wbsmpCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Placeholder
        return 1;
    }
}
