package skycat.wbshop.server;

public class WorldBorderHelper {
    /**
     * Updates the world border based on the aggregate number of points held
     * @param economyManager The economy manager where wallets are held
     * @return Whether the operation succeeded
     */
    public static boolean updateWorldBorder(EconomyManager economyManager) {
        if (WBShopServer.SERVER_INSTANCE != null) {
            WBShopServer.SERVER_INSTANCE.getOverworld().getWorldBorder().setSize(calcDesiredBorderWidth(economyManager)); // TODO: Good start, definitely not complete
            return true;
        }
        return false;
    }

    private static double calcDesiredBorderWidth(EconomyManager economyManager) {
        double blocks = economyManager.getTotalBalance()/7.0; // 1 block per 7 points TODO: Currently hardcoded, want to change that.
        if (blocks > 9) {
            return Math.sqrt(blocks);
        } else {
            return 3;
        }
    }
}
