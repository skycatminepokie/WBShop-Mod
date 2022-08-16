package skycat.wbshop.shop;

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
public class OfferManager {
    public static ArrayList<Offer> getOfferList() {
        return WBShopServer.SETTINGS.getOfferList();
    }

    /**
     * Gets the highest-paying unfilled {@link Offer} for an {@link Item}
     *
     * @param item The type of item the offer should be for.
     * @return The highest-paying {@link Offer}, or {@code null} if there is no valid offer.
     */
    @CheckForNull // Is this used correctly?
    public static Offer getTopOffer(Item item) {
        Offer topOffer = null;

        for (Offer offer : getOfferList()) {
            // Taking advantage of lazy boolean ops, we won't attempt to access methods of topOffer if it is null
            if (offer.getItem().equals(item) && (topOffer == null || (offer.getPointsPerItem() > topOffer.getPointsPerItem())) && !offer.isFilled()) {
                topOffer = offer;
            }
        }

        return topOffer;
    }

    public static void registerOffer(Offer offer) {
        getOfferList().add(offer);
    }

    public static void registerOffers(ArrayList<Offer> offers) {
        getOfferList().addAll(offers);
    }

    public static void registerOffers(Offer... offers) {
        getOfferList().addAll(List.of(offers));
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
            giveItems(itemType, sellAmount, player);
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
     * Attempts to put items in a player's inventory. Drops them on the ground when the player has no space left.
     * @param itemType The type of item to give.
     * @param amount The number of items to give (must be positive).
     * @param player The player to give the items to.
     */
    public static void giveItems(Item itemType, int amount, PlayerEntity player) {
        while (amount >= itemType.getMaxCount()) {
            player.getInventory().offerOrDrop(new ItemStack(itemType, itemType.getMaxCount()));
            amount -= itemType.getMaxCount();
        }
        if (amount > 0) {
            player.getInventory().offerOrDrop(new ItemStack(itemType, amount));
        }
    }

    public static void claimPurchases(PlayerEntity player) {
        getOfferList().forEach(offer -> {
            if (offer.isFilled() && (offer.getOwner().equals(player.getUuid()))) { // TODO: Potentially optimize? I don't know which check is faster in most cases.
                giveItems(offer.getItem(), offer.getItemsRequested(), player);
                // Logging UUID instead of name so that it is harder for server admins to get potential "spoilers" if they are trying to avoid them. May add an option to change this in the future.
                WBShopServer.LOGGER.info("Offer " + offer.getId() + " for " + offer.getItemsRequested() + "x " + offer.getItem().getName().getString() + " claimed by player with UUID " + player.getUuid());
                getOfferList().remove(offer); // TODO: I think this is showing an error to the player because of how it is being deleted.
            }
        });
    }
}
