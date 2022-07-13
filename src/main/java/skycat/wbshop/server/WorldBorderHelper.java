package skycat.wbshop.server;

import skycat.wbshop.WBShopServer;

public class WorldBorderHelper {

    private static double calcDesiredBorderWidth(EconomyManager economyManager) {
        double blocks = economyManager.getTotalBalance() / WBShopServer.SETTINGS.pointsPerBlock;
        if (blocks > 3) {
            return blocks;
        } else {
            return 3; // Must be at least 3 blocks wide
        }
    }

    public static void setPointsPerBlock(double points) {
        WBShopServer.SETTINGS.pointsPerBlock = points;
    }

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
}
