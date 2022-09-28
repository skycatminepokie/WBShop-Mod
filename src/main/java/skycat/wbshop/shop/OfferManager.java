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
     *
     * @param itemType   The type of item that is being sold.
     * @param sellAmount The number of items being sold.
     * @return The number of points the sale was worth.
     * @deprecated This makes new ItemStacks instead of manipulating old ones, leading to issues with NBT data. Use {@link OfferManager#sellOrReturn(ItemStack, PlayerEntity)} instead.
     */
    @Deprecated public static int sellOrReturn(Item itemType, int sellAmount, PlayerEntity player) {
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

    /**
     * Tries to fill the best offer for the type of item. Returns unsold items to the player.
     * @param itemStack The items to sell.
     * @param player The player that is selling the items.
     * @return The number of points to be awarded for selling the items.
     */
    public static int sellOrReturn(ItemStack itemStack, PlayerEntity player) { // TODO: Test
        if (canSell(itemStack)) {
            Item itemType = itemStack.getItem();
            int points = 0;
            Offer topOffer = getTopOffer(itemType);
            int sellAmount = itemStack.getCount();

            if (topOffer == null) { // There are no remaining offers for the item
                // Give the player their unsold items back
                WBShopServer.LOGGER.info("Player attempted to sell " + itemStack.getName().getString() + " x" + itemStack.getCount() + ", but there were no offers.");
                giveItemStack(itemStack, player);
                return 0;
            }

            int unfilledVolume = topOffer.getUnfilled();

            if (sellAmount > unfilledVolume) { // If we try to sell more than the top offer...
                itemStack.setCount(sellAmount - unfilledVolume); // Sell the items we can...
                points += topOffer.tryToFill(unfilledVolume); // Award the points...
                points += sellOrReturn(itemStack, player); // Then try to find the next top offer
            } else {
                points += topOffer.tryToFill(sellAmount); // Sell all the items
            }
            WBShopServer.LOGGER.info("Player sold " + itemStack.getName().getString() + " x" + itemStack.getCount() + " successfully.");
            return points;
        } else {
            giveItemStack(itemStack, player);
            return 0;
        }
    }

    /**
     * Try to sell multiple item stacks on behalf of a player.
     * (Calls the single-stack method multiple times and adds the results.)
     * @param itemStacks The ItemStacks containing the items to sell.
     * @param player The player that is selling the items.
     * @return The total value of successfully sold items (the amount paid).
     */
    public static int sellOrReturn(List<ItemStack> itemStacks, PlayerEntity player) {
        int total = 0;
        for (ItemStack itemStack : itemStacks) {
            total += sellOrReturn(itemStack, player);
        }
        return total;
    }

    public static void sellScreenClosing(SellScreenHandler handler, PlayerEntity player) {
        EconomyManager.getInstance().addBalance(player, sellOrReturn(handler.getStacks().subList(0, 54), player)); // Magic numbers 0 and 54: Selling slots 0-53 (everything in the double chest)
        WBShopServer.LOGGER.info("Player " + player.getName().getString() + " closed sell gui " + handler.syncId + " at location X: " + (int) player.getPos().getX() + " Y: " + (int) player.getPos().getY() + " Z: " + (int) player.getPos().getZ());
    }

    /**
     * Attempts to put items in a player's inventory. Drops them on the ground when the player has no space left.
     *
     * @param itemType The type of item to give.
     * @param amount   The number of items to give (must be positive).
     * @param player   The player to give the items to.
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

    public static void giveItemStack(ItemStack itemStack, PlayerEntity player) {
        player.getInventory().offerOrDrop(itemStack);
    }

    public static void claimPurchases(PlayerEntity player) {
        ArrayList<Offer> toDelete = new ArrayList<>();
        getOfferList().forEach(offer -> {
            if (offer.isFilled() && (offer.getOwner().equals(player.getUuid()))) { // TODO: Potentially optimize? I don't know which check is faster in most cases.
                giveItems(offer.getItem(), offer.getItemsRequested(), player);
                // Logging UUID instead of name so that it is harder for server admins to get potential "spoilers" if they are trying to avoid them. May add an option to change this in the future.
                WBShopServer.LOGGER.info("Offer " + offer.getId() + " for " + offer.getItemsRequested() + "x " + offer.getItem().getName().getString() + " claimed by player with UUID " + player.getUuid());
                toDelete.add(offer);
            }
        });
        getOfferList().removeAll(toDelete);
    }

    public static boolean canSell(ItemStack itemStack) {
        if (itemStack.hasTag()) { // If it has NBT, it cannot be sold
            WBShopServer.LOGGER.debug("Someone tried to sell " + itemStack + ", but it was rejected because it had NBT data.");
            return false;
        }
        return itemStack.getItem() != Items.AIR;

    }
}
