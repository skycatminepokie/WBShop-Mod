package skycat.wbshop.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import skycat.wbshop.WBShopServer;
import skycat.wbshop.server.Vote;

import java.time.LocalDateTime;
import java.util.UUID;

import static skycat.wbshop.WBShopServer.ECONOMY_MANAGER;
import static skycat.wbshop.WBShopServer.VOTE_MANAGER;

public class VoteCommandHandler {
    // Called with policy and amount args
    public static int calledWithAmount(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Placeholder
        // TODO: Optimize
        int amount = IntegerArgumentType.getInteger(context, "amount");
        UUID uuid = context.getSource().getPlayer().getUuid();
        Vote vote = new Vote(uuid, amount, LocalDateTime.now());
        VOTE_MANAGER.addVote(vote, context.getArgument("policy", int.class));
        ECONOMY_MANAGER.removeBalance(uuid, amount); // TODO: Can't detect failure yet
        context.getSource().getPlayer().sendMessage(Text.of("Success!"), false);
        WBShopServer.LOGGER.info("Player " + context.getSource().getPlayer().getName().getString() + " voted for policy #" + IntegerArgumentType.getInteger(context, "policy") + " with " + IntegerArgumentType.getInteger(context, "amount") + " points."); // I'm not sure about whether "policy" has to be the same object as when we used it to register
        return 1;
    }

    // Called with just policy arg
    public static int calledWithPolicy(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Placeholder
        return 1;
    }

    public static int voteCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // Called with no args
        // TODO: Placeholder
        // TODO: Better explanation
        context.getSource().getPlayer().sendMessage(Text.of("Use this command to vote for policies."), false);
        // TODO: Needs to list available policies
        return 1;
    }
}
