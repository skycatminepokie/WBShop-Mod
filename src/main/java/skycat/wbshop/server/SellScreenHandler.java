package skycat.wbshop.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import skycat.wbshop.shop.OfferManager;

@Environment(EnvType.SERVER)
public class SellScreenHandler extends GenericContainerScreenHandler {
    boolean beenClosed = false;

    public SellScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
        super(type, syncId, playerInventory, inventory, rows);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void close(PlayerEntity player) {
        if (!beenClosed) {
            OfferManager.sellScreenClosing(this, player);
            beenClosed = true;
            super.close(player);
        } // Maybe add logging for double-closing
    }
}
