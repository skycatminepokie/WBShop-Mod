package skycat.wbshop.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

import static skycat.wbshop.WBShopServer.ECONOMY_MANAGER;
import static skycat.wbshop.util.WBShopAbstracter.textOf;

public class BalCommandHandler {

    /**
     * Tells the player their current balance
     *
     * @return 0: Initialized a new wallet. 1: Success
     */
    public static int balCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity thePlayer = context.getSource().getPlayer();
        int points;
        try {
            points = ECONOMY_MANAGER.getBalance(thePlayer.getUuid());
        } catch (IllegalArgumentException e) {
            context.getSource().sendFeedback(textOf("You have no points."), false);
            ECONOMY_MANAGER.initializeWallet(thePlayer.getUuid());
            return 0;
        }

        context.getSource().sendFeedback(
                textOf(switch (points) {
                    case 0 -> "You have no points.";
                    case 1 -> "You have 1 point.";
                    default -> "You have " + points + " points.";
                }),
                false);
        return 1;
    }
}
