package skycat.wbshop;

import skycat.wbshop.server.EconomyManager;
import skycat.wbshop.server.VoteManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class WBShopSaverLoader {
    public static final String SAVE_FILE_NAME = "wbshop_mod_save_test.txt"; // WARN: "test" is for debug
    private WBShopSave save;

    /**
     * Default constructor. Loads the save from file and updates settings.
     * @throws IOException
     */
    public WBShopSaverLoader() throws IOException {
        save = this.readSave();
    }

    /**
     * Update the data to save, then save it to file.
     *
     * @return Success
     */
    public boolean saveToFile() throws FileNotFoundException {
        save.economyManager = WBShopServer.ECONOMY_MANAGER;
        save.voteManager = WBShopServer.VOTE_MANAGER;
        save.settings = WBShopServer.SETTINGS;

        PrintWriter printWriter = new PrintWriter(SAVE_FILE_NAME);
        printWriter.println(WBShopServer.GSON.toJson(save));
        printWriter.close();
        return true;
    }

    /**
     * Reads the save into memory. This should not have any effect on the game. To load settings from the save, see {@link #loadSave()}.
     */
    private WBShopSave readSave() {
        Scanner scanner;
        // Read the save from file
        try { // TODO: Clean up exception handling.
            scanner = new Scanner(new File(SAVE_FILE_NAME));
            save = WBShopServer.GSON.fromJson(scanner.nextLine(), WBShopSave.class);
            scanner.close();
        } catch (FileNotFoundException|NoSuchElementException e) {
            System.out.println("No save file found, defaulting to new save. Stack trace:");
            e.printStackTrace();
            save = new WBShopSave(Settings.DEFAULT_SETTINGS, new VoteManager(), new EconomyManager());
        }
        return save;
    }

    /**
     * Loads information and configuration from file into the game.
     */
    public void loadSave() {
        // Load info into game
        WBShopServer.ECONOMY_MANAGER = save.economyManager;
        WBShopServer.VOTE_MANAGER = save.voteManager;
        WBShopServer.SETTINGS = save.settings;
    }

    /**
     * Wrapper class to help with saving and loading
     */
    private class WBShopSave {
        protected Settings settings;
        protected VoteManager voteManager;
        protected EconomyManager economyManager;

        public WBShopSave(Settings settings, VoteManager voteManager, EconomyManager economyManager) {
            this.settings = settings;
            this.voteManager = voteManager;
            this.economyManager = economyManager;
        }
    }

}
