package skycat.wbshop.shop;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import skycat.wbshop.WBShopServer;
import skycat.wbshop.server.EconomyManager;
import skycat.wbshop.server.SellScreenHandler;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to manage, keep track of, and search through {@link Offer} objects.
 */
public class OfferManager { // TODO: Test
    @Getter
    private static ArrayList<Offer> offerList = new ArrayList<>(); // TODO: Proper loading system

    /**
     * Gets the highest-paying unfilled {@link Offer} for an {@link Item}
     *
     * @param item The type of item the offer should be for.
     * @return The highest-paying {@link Offer}, or {@code null} if there is no valid offer.
     */
    @CheckForNull // Is this used correctly?
    public static Offer getTopOffer(Item item) {
        Offer topOffer = null;

        for (Offer offer : offerList) {
            // Taking advantage of lazy boolean ops, we won't attempt to access methods of topOffer if it is null
            if (offer.getItem().equals(item) && (topOffer == null || (offer.getPointsPerItem() > topOffer.getPointsPerItem())) && !offer.isFilled()) {
                topOffer = offer;
            }
        }

        return topOffer;
    }

    public static void registerOffer(Offer offer) {
        offerList.add(offer);
    }

    public static void registerOffers(ArrayList<Offer> offers) {
        offerList.addAll(offers);
    }

    public static void registerOffers(Offer... offers) {
        offerList.addAll(List.of(offers));
    }

    /**
     * Tries to fill the best offer for the type of item. Returns unsold items to the player.
     * @param itemType The type of item that is being sold.
     * @param sellAmount The number of items being sold.
     * @return The number of points the sale was worth.
     */
    public static int sellOrReturn(Item itemType, int sellAmount, PlayerEntity player) {
        int points = 0;
        Offer topOffer = getTopOffer(itemType);
        if (itemType == Items.AIR) {
            return 0;
        }
        if (topOffer == null) { // There are no remaining offers for the item
            // Give the player their unsold items back
            returnItems(itemType, sellAmount, player);
            return 0;
        }

        int unfilledVolume = topOffer.getUnfilled();

        if (sellAmount > unfilledVolume) { // Trying to sell more than the top offer
            sellAmount -= unfilledVolume;
            points += topOffer.tryToFill(unfilledVolume); // Fill the offer, then...
            points += sellOrReturn(itemType, sellAmount, player); // Try to find the next top offer
        } else {
            points += topOffer.tryToFill(sellAmount); // Fill as much of the offer as we need
            // sellAmount = 0 // sellAmount should be set to 0 at this point, but it isn't accessed again.
        }
        return points;
    }

    public static int sellOrReturn(ItemStack itemStack, PlayerEntity player) {
        return sellOrReturn(itemStack.getItem(), itemStack.getCount(), player);
    }

    public static int sellOrReturn(List<ItemStack> itemStacks, PlayerEntity player) {
        int total = 0;
        for (ItemStack itemStack : itemStacks) {
            total += sellOrReturn(itemStack, player);
        }
        return total;
    }

    public static void sellScreenClosing(SellScreenHandler handler, PlayerEntity player) {
        EconomyManager.getInstance().addBalance(player, sellOrReturn(handler.getStacks().subList(0,54), player)); // Magic numbers 0 and 54: Selling slots 0-53 (everything in the double chest)
    }

    /**
     * Attempts to return items to a player's inventory. Drops them on the ground when the player has no space left.
     * @param itemType The type of item to return.
     * @param amount The number of items to return (must be positive).
     * @param player The player to return the items to.
     */
    public static void returnItems(Item itemType, int amount, PlayerEntity player) {
        while (amount >= itemType.getMaxCount()) {
            player.getInventory().offerOrDrop(new ItemStack(itemType, itemType.getMaxCount()));
            amount -= itemType.getMaxCount();
        }
        if (amount > 0) {
            player.getInventory().offerOrDrop(new ItemStack(itemType, amount));
        }
    }

}
