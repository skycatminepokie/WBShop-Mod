package skycat.wbshop.shop;

import lombok.Getter;
import net.minecraft.item.Item;
import skycat.wbshop.WBShopServer;

import java.util.UUID;

/**
 * An object representing an offer made to buy some of an item.
 */
public class Offer {
    /**
     * A unique ID for the offer
     */
    @Getter private final long id;
    /**
     * The creator of the offer
     */
    @Getter private final UUID owner;
    /**
     * The type of item that the offer can be satisfied with
     */
    @Getter private final int itemId;
    /**
     * The number of items requested by the creator of the offer.
     */
    @Getter private final int itemsRequested;
    /**
     * The current total of items collected to fill the offer
     */
    @Getter private int itemsFilled = 0;
    /**
     * The number of points offered by the owner per item.
     */
    @Getter private final double pointsPerItem;
    @Getter private boolean isFilled = false;

    /**
     * Create a new {@link Offer}.
     *
     * @param owner          The creator of the offer.
     * @param item           The item requested.
     * @param pointsPerItem  The points offered per item. Must be greater than 0.
     * @param itemsRequested The number of items wanted. Must be greater than 0.
     * @throws IllegalArgumentException If {@code pointsPerItem <= 0} or {@code itemsRequested <= 0}.
     */
    public Offer(UUID owner, Item item, double pointsPerItem, int itemsRequested) {
        this.id = Offer.nextId();
        this.owner = owner;
        this.itemId = Item.getRawId(item);
        if (itemsRequested <= 0) {
            throw new IllegalArgumentException("itemsRequested must be greater than 0, but got " + itemsRequested);
        }
        this.itemsRequested = itemsRequested;
        if (pointsPerItem <= 0) {
            throw new IllegalArgumentException("pointsPerItem must be greater than 0, but got " + pointsPerItem);
        }
        this.pointsPerItem = pointsPerItem;
    }

    /**
     * Get the next unused offer id.
     *
     * @return The next unused offer id.
     */
    private static long nextId() {
        return ++WBShopServer.SETTINGS.lastOfferId; // TODO: Probably should encapsulate the offer id better
    }

    /**
     * Attempt to exchange items for points through the offer.
     *
     * @param numOfItems The number items that are being sold to fill the offer
     * @return The number of points to be paid for the offer.
     * @throws IllegalArgumentException If {@code numOfItems} is greater than the unfilled volume of the {@link Offer}.
     */
    public int tryToFill(int numOfItems) {
        if (numOfItems > (itemsRequested - itemsFilled)) {
            throw new IllegalArgumentException("Offer does not accept this many items");
        } else {
            fill(numOfItems);
            return (int) (numOfItems * pointsPerItem); // Will always floor the point reward, resulting in some loss at some times. This is intentional.
        }
    }

    /**
     * Update the number of items exchanged through the offer.
     * Does not ensure validity! Use {@link Offer#tryToFill(int)} to do this.
     *
     * @param numOfItems The number of newly exchanged items.
     */
    private void fill(int numOfItems) {
        itemsFilled += numOfItems;
        if (itemsFilled >= itemsRequested) { // The offer has been filled
            this.isFilled = true;
            if (itemsFilled > itemsRequested) { // The offer has been overfilled (try not to let this happen)
                WBShopServer.LOGGER.warn("itemsFilled (" + itemsFilled + ") is greater than itemsRequested (" + itemsRequested + ") by offer " + id + " (more items were exchanged than requested).");
            }
        }
    }

    /**
     * Get the number of items that are still needed to fill the offer.
     *
     * @return The number of items that are still needed to fill the offer.
     */
    public int getUnfilled() {
        return itemsRequested - itemsFilled;
    }

    public Item getItem() {
        return Item.byRawId(itemId);
    }
}
