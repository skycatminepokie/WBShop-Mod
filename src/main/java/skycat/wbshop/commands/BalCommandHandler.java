package skycat.wbshop.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static skycat.wbshop.WBShopServer.ECONOMY_MANAGER;

public class BalCommandHandler {
    public static int balCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        PlayerEntity thePlayer = context.getSource().getPlayer();
        thePlayer.sendMessage(Text.of("You have " + ECONOMY_MANAGER.getBalance(thePlayer.getUuid()) + " points."),false);
        return 0;
    }
}
