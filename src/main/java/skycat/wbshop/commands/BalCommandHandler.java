package skycat.wbshop.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static skycat.wbshop.WBShopServer.ECONOMY_MANAGER;

public class BalCommandHandler {

    /**
     * @param context
     * @return 0: Initialized a new wallet. 1: Success
     * @throws CommandSyntaxException
     */
    public static int balCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity thePlayer = context.getSource().getPlayer();
        int points;
        try {
            points = ECONOMY_MANAGER.getBalance(thePlayer.getUuid());
        } catch (IllegalArgumentException e) {
            thePlayer.sendMessage(Text.of("You have no points."), false);
            ECONOMY_MANAGER.initializeWallet(thePlayer.getUuid());
            return 0;
        }

        thePlayer.sendMessage(
                Text.of(switch (points) {
                    case 0 -> "You have no points.";
                    case 1 -> "You have 1 point.";
                    default -> "You have " + points + " points.";
                }),
                false);
        return 1;
    }
}
