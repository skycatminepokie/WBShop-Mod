package skycat.wbshop.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import skycat.wbshop.WBShopServer;
import skycat.wbshop.shop.Offer;
import skycat.wbshop.shop.OfferManager;

public class OfferCommandHandler {
    /**
     * The command /offer list
     * @param context
     * @return
     * @throws CommandSyntaxException
     */
    public static int list(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO
        return 0;
    }

    public static int create(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO
        return Command.SINGLE_SUCCESS;
    }

    public static int createWithArgs(ServerCommandSource source, Item itemType, double pointsPerItem, int itemCount) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        int totalCost = (int) Math.ceil(pointsPerItem * itemCount); // (rounded up - there will be some loss)
        // Check if player has enough points
        if (WBShopServer.ECONOMY_MANAGER.getBalance(player) < totalCost) {
            throw new SimpleCommandExceptionType(new LiteralMessage("You don't have enough points for that.")).create();
        }
        // Deduct points
        WBShopServer.ECONOMY_MANAGER.removeBalance(player, totalCost);

        // Create and register the offer
        OfferManager.registerOffer(new Offer(player.getUuid(), itemType, pointsPerItem, itemCount));

        // Send feedback
        source.sendFeedback(Text.of("Created an offer for " + itemCount + "x " + itemType.getName().getString() + " at " + pointsPerItem + " points per item (" + totalCost + " points in total)"), false);

        return Command.SINGLE_SUCCESS;
    }

    public static int claim(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return 1; // TODO
    }
}
