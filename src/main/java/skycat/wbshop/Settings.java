package skycat.wbshop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Settings {
    public double pointsPerBlock = 3;
    public static final String SETTINGS_FILE_NAME = "wbshop_settings.txt";

    private Settings() {}

    public void save() {
        try {
            PrintWriter printWriter = new PrintWriter(SETTINGS_FILE_NAME);
            printWriter.println(WBShopServer.GSON.toJson(this));
            printWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Failed to save settings. Dumping info:");
            System.out.println("pointsPerBlock:" + pointsPerBlock);
            throw new RuntimeException(e);
        }
    }

    public static Settings load() {
        Settings loaded;
        Scanner scanner;
        try {
            scanner = new Scanner(new File(SETTINGS_FILE_NAME));
            loaded = WBShopServer.GSON.fromJson(scanner.nextLine(), Settings.class);
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load settings. Creating new settings.");
            return new Settings();
        }
        return loaded;
    }
}
