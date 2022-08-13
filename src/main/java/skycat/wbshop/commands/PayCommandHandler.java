package skycat.wbshop.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import skycat.wbshop.WBShopServer;
import skycat.wbshop.server.EconomyManager;

import java.util.Collection;
import java.util.UUID;

public class PayCommandHandler {
    public static int payCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Placeholder
        return 1;
    }

    public static int payCalledWithArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        UUID sentFrom = context.getSource().getPlayer().getUuid();
        Collection<GameProfile> sendTo = GameProfileArgumentType.getProfileArgument(context, "player");
        int amount = context.getArgument("amount", int.class);

        if (sendTo.size() != 1) {
            throw new SimpleCommandExceptionType(new LiteralMessage("You can only pay one player at a time.")).create(); // TODO: Allow multiple recipients?
        }
        if (EconomyManager.getInstance().getBalance(sentFrom) >= amount) {
            EconomyManager.getInstance().transferBalance(sentFrom, ((GameProfile) sendTo.toArray()[0]).getId(), amount);
        } else {
            throw new SimpleCommandExceptionType(new LiteralMessage("You don't have enough points for that!")).create();
        }

        context.getSource().sendFeedback(Text.of("Successfully transferred " + amount + " points."), false);

        return 1;
    }
}
