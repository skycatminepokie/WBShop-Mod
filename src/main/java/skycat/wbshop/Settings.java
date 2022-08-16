package skycat.wbshop;

import skycat.wbshop.shop.Offer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Settings {
    public static String SETTINGS_FILE_NAME = "wbshop_settings.txt"; // TODO: Updating from pre-offer versions might break everything. This is not ideal.
    public double pointsPerBlock = 3;
    public ArrayList<Offer> offerList;
    public long lastOfferId = -1;

    private Settings() {
        offerList = new ArrayList<Offer>();
    }

    public static Settings load() {
        // TODO: Handle loading errors and inconsistencies caused by crashes and other things
        Settings loaded;
        Scanner scanner;
        try {
            scanner = new Scanner(new File(SETTINGS_FILE_NAME));
            loaded = WBShopServer.GSON.fromJson(scanner.nextLine(), Settings.class);
            scanner.close();
        } catch (FileNotFoundException e) {
            WBShopServer.LOGGER.warn("Failed to load settings. Creating new settings.");
            return new Settings();
        }
        return loaded;
    }

    public long getLastOfferId() {
        return lastOfferId;
    }

    public void save() {
        try {
            PrintWriter printWriter = new PrintWriter(SETTINGS_FILE_NAME);
            printWriter.println(WBShopServer.GSON.toJson(this));
            printWriter.close();
        } catch (FileNotFoundException e) {
            WBShopServer.LOGGER.error("Failed to save settings. Dumping info:\npointsPerBlock:" + pointsPerBlock);
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Offer> getOfferList() {
        return offerList;
    }
}
