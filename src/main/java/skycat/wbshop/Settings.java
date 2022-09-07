package skycat.wbshop;

import skycat.wbshop.shop.Offer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Settings {
    public static final File SETTINGS_FILE = new File("wbshop_settings.txt");
    public double pointsPerBlock = 3;
    public ArrayList<Offer> offerList = new ArrayList<>();
    public long lastOfferId = -1;
    public boolean enableLogging = true;

    private Settings() {

    }

    public static Settings load() {
        // TODO: Handle loading errors and inconsistencies caused by crashes and other things
        Settings loaded;
        Scanner scanner;
        try {
            scanner = new Scanner(SETTINGS_FILE);
            if (scanner.hasNextLine()) {
                loaded = WBShopServer.GSON.fromJson(scanner.nextLine(), Settings.class);
            } else {
                WBShopServer.LOGGER.warn("Failed to load settings. Creating new settings.");
                return new Settings();
            }
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
            PrintWriter printWriter = new PrintWriter(SETTINGS_FILE);
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
