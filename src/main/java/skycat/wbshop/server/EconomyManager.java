package skycat.wbshop.server;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

/**
 * A class to manage the balances of all players.
 * Initialized in {@link WBShopServer#onInitializeServer()}
 */
public class EconomyManager {
    public HashMap<UUID, @NotNull Integer> wallets;
    public static final File SAVE_FILE = new File("WBShopEconomyManagerSave.txt");

    /**
     * Get a player's WBShop balance.
     * @param uuid The player's UUID
     * @return The player's balance.
     * @throws IllegalArgumentException If the uuid does not have a valid entry associated with it
     */
    public int getBalance(UUID uuid) {
        if (!isValidEntry(uuid)) {
            throw new IllegalArgumentException("uuid does not have a valid entry");
        }

        return wallets.get(uuid);
    }

    /**
     * Decrease a player's WBShop balance.
     * @param uuid The player's UUID
     * @param amount The number of points to remove
     * @return The number of points the player has left over.
     * @throws IllegalArgumentException If the uuid does not have a valid entry associated with it
     */
    public int removeBalance(UUID uuid, int amount) {
        if (isValidEntry(uuid)) {
            throw new IllegalArgumentException("uuid does not have a valid entry");
        }
        wallets.put(uuid, wallets.get(uuid) - amount);
        return wallets.get(uuid);
    }

    /**
     * Give points to a player
     * @param uuid The player's UUID
     * @param amount The amount to give
     * @return The player's new balance
     */
    public int addBalance(UUID uuid, int amount) {
        if (!isValidEntry(uuid)) {
            throw new IllegalArgumentException("uuid does not have a valid entry");
        }
        wallets.put(uuid, wallets.get(uuid) + amount);
        return wallets.get(uuid);

    }

    /**
     * Transfer a portion of a balance from one player to another.
     * @param from The UUID of the player that is sending the balance
     * @param to The UUID of the player that is receiving the balance
     * @param amount The amount to transfer
     * @return Returns true if the transfer succeeded
     * @throws IllegalArgumentException If the uuid does not have a valid entry associated with it
     */
    public boolean transferBalance(UUID from, UUID to, int amount) {
        if (!isValidEntry(from)) {
            throw new IllegalArgumentException("\"from\" UUID is invalid");
        }
        if (!isValidEntry(to)) {
            throw new IllegalArgumentException("\"to\" UUID is invalid");
        }

        removeBalance(from, amount);
        addBalance(to, amount);

        return true;
    }

    /**
     * Get the instance of this class that is initialized by {@link WBShopServer}.
     * @return The instance
     */
    public static EconomyManager getInstance() {
        return WBShopServer.ECONOMY_MANAGER;
    }

    private boolean isValidEntry(UUID key) {
        if (!wallets.containsKey(key)) { // Separated into different cases for easy modification later
            return false; // UUID key is not found
        }
        if (wallets.get(key) == null) {
            return false; // The value should not be null.
        }
        return true;
    }

    /**
     * Loads an {@link EconomyManager} from a save file
     * @return The loaded {@link EconomyManager}
     */
    private static EconomyManager loadFromFile() throws FileNotFoundException {
        String jsonString = "";
        Scanner fileScanner = new Scanner(SAVE_FILE);
        while (fileScanner.hasNextLine()) {
            jsonString += fileScanner.nextLine();
        }
        return WBShopServer.GSON.fromJson(jsonString, EconomyManager.class);
    }

    /**
     * Save this object to file
     * @return Success state
     * @throws FileNotFoundException If the file is not found
     */
    public boolean saveToFile() throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(SAVE_FILE);
        printWriter.write(WBShopServer.GSON.toJson(this));
        printWriter.close();
        return true;
    }

    /**
     * Makes a new EconomyManager. This is to be used ONLY in initialization. TODO: Find a better way to do this
     * @return The new EconomyManager
     */
    public static EconomyManager makeNewManager() {
        try {
            return loadFromFile();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file to load player wallets from, initializing an empty one.");
            return new EconomyManager();
        }
    }

}
