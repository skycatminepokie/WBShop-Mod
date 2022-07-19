package skycat.wbshop.shop;

import lombok.Getter;
import net.minecraft.item.Item;

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

    /*
    public static Offer getTopOffer() {

    }
    */

}
