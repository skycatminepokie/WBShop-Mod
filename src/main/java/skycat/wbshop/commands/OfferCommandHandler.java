package skycat.wbshop.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.item.Item;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import skycat.wbshop.WBShopServer;
import skycat.wbshop.shop.Offer;
import skycat.wbshop.shop.OfferManager;

import static skycat.wbshop.util.WBShopAbstracter.textOf;

public class OfferCommandHandler {
    /**
     * Attempts to claim filled offers owned by the player.
     *
     * @param context The CommandContext. Must be a player.
     * @return 1 for success.
     * @throws CommandSyntaxException If the source is not a player
     */
    public static int claim(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: When successfully claiming an offer, this always gives an error message to the player. Maybe it's because we're deleting the offer as it is being accessed?
        OfferManager.claimPurchases(context.getSource().getPlayer());
        context.getSource().sendFeedback(textOf("Successfully claimed your filled offers."), false);
        return 1; // TODO: Maybe return number of purchases or number of items claimed?
    }

    public static int create(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO
        return Command.SINGLE_SUCCESS;
    }

    public static int createWithArgs(ServerCommandSource source, Item itemType, double pointsPerItem, int itemCount) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
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
        source.sendFeedback(textOf("Created an offer for " + itemCount + "x " + itemType.getName().getString() + " at " + pointsPerItem + " points per item (" + totalCost + " points in total)"), false);

        return Command.SINGLE_SUCCESS;
    }

    /**
     * The command /offer list
     *
     * @param context The command context
     * @return Success
     * @throws CommandSyntaxException
     */
    public static int list(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(textOf("Offers: "), false);
        OfferManager.getOfferList().forEach(offer -> {
            if (offer.getUnfilled() > 0) {
                context.getSource().sendFeedback(textOf("" + offer.getUnfilled() + "x " + offer.getItem().getName().getString() + " for " + offer.getPointsPerItem() + " points per item."), false);
            }
        });
        return 1;
    }
}
