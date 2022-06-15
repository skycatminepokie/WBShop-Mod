package skycat.wbshop.server;

import skycat.wbshop.WBShopServer;

public class WorldBorderHelper {
    public static double POINTS_PER_BLOCK = 3.0; // Default 3 points per block

    /**
     * Updates the world border based on the aggregate number of points held
     *
     * @param economyManager The economy manager where wallets are held
     * @return Whether the operation succeeded
     */
    public static boolean updateWorldBorder(EconomyManager economyManager) {
        if (WBShopServer.SERVER_INSTANCE != null) {
            WBShopServer.SERVER_INSTANCE.getOverworld().getWorldBorder().setSize(calcDesiredBorderWidth(economyManager));
            return true;
        }
        return false;
    }

    private static double calcDesiredBorderWidth(EconomyManager economyManager) {
        double blocks = economyManager.getTotalBalance() / POINTS_PER_BLOCK;
        if (blocks > 9) {
            return Math.sqrt(blocks);
        } else {
            return 3;
        }
    }

    public static void setPointsPerBlock(double points) { // TODO: This isn't ever loaded in. Need to make saving/loading class.
        POINTS_PER_BLOCK = points;
    }
}
