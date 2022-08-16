package skycat.wbshop.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import skycat.wbshop.WBShopServer;

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
public class EconomyManager { // TODO: Ensure all wallets are always initialized
    public static final File SAVE_FILE = new File("WBShopEconomyManagerSave.txt");
    public static double POINT_LOSS = 0.1; // Default lose 10% of points on death
    public HashMap<UUID, @NotNull Integer> wallets;

    /**
     * Get the instance of this class that is initialized by {@link WBShopServer}.
     *
     * @return The instance
     */
    public static EconomyManager getInstance() {
        return WBShopServer.ECONOMY_MANAGER;
    }

    /**
     * Loads an {@link EconomyManager} from a save file
     *
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
     * Makes a new EconomyManager. This is to be used ONLY in initialization. TODO: Find a better way to do this. Potentially move to main mod class?
     *
     * @return The new EconomyManager
     */
    public static EconomyManager makeNewManager() {
        EconomyManager manager = null;
        try {
            manager = loadFromFile();
        } catch (FileNotFoundException e) {
            WBShopServer.LOGGER.info("Could not find file to load player wallets from.");
        }
        if (manager == null) {
            WBShopServer.LOGGER.info("Initializing a new player wallet save file.");
            manager = new EconomyManager();
            manager.wallets = new HashMap<>();
        }
        return manager;
    }

    /**
     * Give points to a player
     *
     * @param uuid   The player's UUID
     * @param amount The amount to give
     * @return The player's new balance
     */
    public int addBalance(UUID uuid, int amount) {
        if (!isValidEntry(uuid)) {
            WBShopServer.LOGGER.warn("uuid was not valid in addBalance. Initializing an empty wallet.");
            initializeWallet(uuid);
        }
        wallets.put(uuid, wallets.get(uuid) + amount);
        return wallets.get(uuid);
    }

    public int addBalance(PlayerEntity player, int amount) {
        return addBalance(player.getUuid(), amount);
    }

    public int getBalance(ServerPlayerEntity player) {
        return getBalance(player.getUuid());
    }

    /**
     * Get a player's WBShop balance.
     *
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

    public int getTotalBalance() {
        int total = 0;
        for (UUID uuid : wallets.keySet()) { // TODO: This is a hotfix, want to use .foreach
            total += wallets.get(uuid);
        }
        return total;
    }

    public boolean hasWallet(ServerPlayerEntity player) {
        return isValidEntry(player.getUuid());
    }

    /**
     * Makes a new wallet and sets the balance to zero.
     *
     * @param uuid The {@link UUID} to associate with the wallet
     * @return {@code true} if the wallet was initialized, {@code false} if the wallet already existed (and so was not initialized)
     */
    public boolean initializeWallet(UUID uuid) {
        if (isValidEntry(uuid)) {
            WBShopServer.LOGGER.warn("initializeWallet was called, but the wallet for uuid " + uuid.toString() + " is already initialized!");
            return false;
        } else {
            wallets.put(uuid, 0);
            return true;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isValidEntry(UUID key) {
        if (!wallets.containsKey(key)) { // Separated into different cases for easy modification later
            return false; // UUID key is not found
        }
        return wallets.get(key) != null; // The value should not be null.
    }

    public void onPlayerDeath(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (!isValidEntry(uuid)) {
            this.initializeWallet(uuid);
        }
        int pointsLost = (int) (getBalance(uuid) * (POINT_LOSS));  // Lose POINT_LOSS * balance points on death (ex if POINT_LOSS = 0.1, lose 10% of points on death)
        int pointsLeft = removeBalance(uuid, pointsLost);
        player.sendMessage(Text.of("You died and lost " + pointsLost + (pointsLost == 1 ? " point" : " points") + "! You have " + pointsLeft + (pointsLeft == 1 ? " point" : " points") + " left."));
    }

    /**
     * Decrease a player's WBShop balance.
     *
     * @param uuid   The player's UUID
     * @param amount The number of points to remove
     * @return The number of points the player has left over.
     * @throws IllegalArgumentException If the uuid does not have a valid entry associated with it
     */
    public int removeBalance(UUID uuid, int amount) {
        if (!isValidEntry(uuid)) {
            throw new IllegalArgumentException("uuid does not have a valid entry");
        }
        wallets.put(uuid, wallets.get(uuid) - amount);
        return wallets.get(uuid);
    }

    public int removeBalance(ServerPlayerEntity player, int amount) {
        return removeBalance(player.getUuid(), amount);
    }

    /**
     * Save this object to file
     *
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
     * Transfer a portion of a balance from one player to another.
     *
     * @param from   The UUID of the player that is sending the balance
     * @param to     The UUID of the player that is receiving the balance
     * @param amount The amount to transfer
     * @return Returns true if the transfer succeeded
     * @throws IllegalArgumentException If the uuid does not have a valid entry associated with it
     */
    public boolean transferBalance(UUID from, UUID to, int amount) {
        if (!isValidEntry(from)) {
            throw new IllegalArgumentException("\"from\" UUID is invalid (or wallet is not initialized)");
        }
        if (!isValidEntry(to)) {
            throw new IllegalArgumentException("\"to\" UUID is invalid (or wallet is not initialized)");
        }


        removeBalance(from, amount);
        addBalance(to, amount);

        return true;
    }
}
