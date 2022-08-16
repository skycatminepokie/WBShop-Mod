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
        context.getSource().sendFeedback(Text.of("/econ: Manage the economy"), false);
        return 1;
    }

    /**
     * Remove points from players through a command
     *
     * @param context The command context.
     * @param update  Whether to update the border after removing points.
     * @return The number of players affected.
     */
    public static int removePoints(CommandContext<ServerCommandSource> context, boolean update) throws CommandSyntaxException {
        Collection<GameProfile> targets = GameProfileArgumentType.getProfileArgument(context, "player");
        for (GameProfile target : targets) {
            int amount = context.getArgument("amount", Integer.class);
            EconomyManager.getInstance().removeBalance(target.getId(), amount);
            context.getSource().sendFeedback(Text.of("Removed " + amount + " points from " + target.getName()), true);
        }
        if (update) {
            WorldBorderHelper.updateWorldBorder(EconomyManager.getInstance());
        }
        return targets.size();
    }

    /**
     * Add points to players through a command.
     *
     * @param context The command context.
     * @param update  Whether to update the border after the operation.
     * @return The number of players affected.
     */
    public static int addPoints(CommandContext<ServerCommandSource> context, boolean update) throws CommandSyntaxException {
        Collection<GameProfile> targets = GameProfileArgumentType.getProfileArgument(context, "player");
        for (GameProfile target : targets) {
            int amount = context.getArgument("amount", Integer.class);
            EconomyManager.getInstance().addBalance(target.getId(), amount);
            context.getSource().sendFeedback(Text.of("Added " + amount + " points to " + target.getName()), false);
        }
        if (update) {
            WorldBorderHelper.updateWorldBorder(EconomyManager.getInstance());
        }
        return targets.size();
    }

    public static int econAdd(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(Text.of("/econ add: Add points to a player's wallet"), false);
        return 1;
    }

    public static int econGet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(Text.of("/econ get: Get the number of points a player has"), false);
        return 1;
    }

    public static int econGetWithArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<GameProfile> targets = GameProfileArgumentType.getProfileArgument(context, "player");
        for (GameProfile target : targets) {
            // WBShopServer.LOGGER.info("id: " + target.getId());
            context.getSource().sendFeedback(Text.of(target.getName() + " has " + EconomyManager.getInstance().getBalance(target.getId()) + " points."), false);
        }
        return 1;
    }

    public static int econRemove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(Text.of("/econ remove: Remove points from a player's wallet"), false);
        return 1;
    }


    public static int setPointsPerBlock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WorldBorderHelper.setPointsPerBlock(context.getArgument("points", Double.class));
        WorldBorderHelper.updateWorldBorder(EconomyManager.getInstance());
        context.getSource().sendFeedback(Text.of("PointsPerBlock updated."), true);
        return 1;
    }

    public static int wbsmpCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Placeholder
        return 1;
    }

    public static int setDeathLoss(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        EconomyManager.POINT_LOSS = context.getArgument("loss", Double.class);
        context.getSource().sendFeedback(Text.of("POINT_LOSS updated."), true);
        return 1;
    }
}
