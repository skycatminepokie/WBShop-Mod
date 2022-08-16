package skycat.wbshop.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import skycat.wbshop.server.EconomyManager;

import java.util.Collection;
import java.util.UUID;

public class PayCommandHandler {
    public static int payCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Placeholder
        return 1;
    }

    public static int payCalledWithArgs(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sender = context.getSource().getPlayer();
        UUID senderUUID = sender.getUuid();
        Collection<GameProfile> sendToCollection = GameProfileArgumentType.getProfileArgument(context, "player");
        int amount = context.getArgument("amount", int.class);

        if (sendToCollection.size() != 1) {
            throw new SimpleCommandExceptionType(new LiteralMessage("You can only pay one player at a time.")).create(); // TODO: Allow multiple recipients?
        }
        GameProfile sendTo = (GameProfile) sendToCollection.toArray()[0];
        if (EconomyManager.getInstance().getBalance(senderUUID) >= amount) {
            EconomyManager.getInstance().transferBalance(senderUUID, sendTo.getId(), amount);
        } else {
            throw new SimpleCommandExceptionType(new LiteralMessage("You don't have enough points for that!")).create();
        }

        context.getSource().sendFeedback(Text.of("Successfully transferred " + amount + (amount == 1 ? " point." : " points.")), false); // TODO: Maybe there should be a better way to choose point vs points
        context.getSource().getServer().getPlayerManager().getPlayer(sendTo.getId()).sendMessage(Text.of(sender.getName().getString() + " sent you " + amount + (amount == 1 ? " point." : " points.")));
        return 1;
    }
}
