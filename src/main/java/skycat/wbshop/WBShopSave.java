package skycat.wbshop;

import skycat.wbshop.shop.Offer;

import java.util.ArrayList;

public class WBShopSave { // TODO
    public static final String SAVE_FILE = "WBShopSave.json";

    public boolean perWorldSave = false;
    public boolean perWorldSettings = false;
    public boolean enableDonating = true;
    public boolean enableWorldborderControl = true;

    public CustomItemSettings customItemSettings = new CustomItemSettings();
    public WorldborderSettings worldborderSettings = new WorldborderSettings();

    // Donating

    // Offers
    public boolean enableOffers = true; // Overrides everything in this category if false
    public long lastOfferId = -1; // TODO: make something better than this id system
    public ArrayList<Offer> offers = new ArrayList<>();

    // Items


    public void save() {

    }

    public static WBShopSave load() {
        // TODO
        return null;
    }

    public class CustomItemSettings {
        public CustomItemPolicy customItemPolicy = CustomItemPolicy.DEFAULT;
        public boolean enableFireBook = true;
        public double fireBookFireballVelocity = 3.0;
        public double fireBookMaxAimError = 0.9;

        public enum CustomItemPolicy {
            DEFAULT, // Custom items work and are obtainable
            ALLOWED, // Custom items work, but are not obtainable through the mod
            DISABLED, // Custom items don't work
            DESTROY, // Custom items don't work, and are destroyed if an attempt to use them is made
            MAKE_NORMAL // Custom items don't work, and have their custom item NBT removed if an attempt to use them is made
        }
    }

    public class WorldborderSettings {
        public double pointsPerBlock = 3.0;
        public BorderSizeAlgorithm borderSizeAlgorithm = BorderSizeAlgorithm.EXPONENTIAL;

        public enum BorderSizeAlgorithm {
            LINEAR, // Each outward expansion of one block costs x points
            EXPONENTIAL // Each additional square block costs x points
        }
    }

    public class DonationSettings {

        public enum DonationEvaluatorType {
            UNIFORM, // X points per item
            WEIGHTED // Some items are worth more than others
        }
    }

}
