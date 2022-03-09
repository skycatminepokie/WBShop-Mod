package skycat.wbshop.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class DonateScreenHandler extends GenericContainerScreenHandler {
    public DonateScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
        super(type, syncId, playerInventory, inventory, rows);
    }

    public static DonateScreenHandler newDonateScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return (DonateScreenHandler) GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory);
    }

    @Override
    public void close(PlayerEntity player) {
        DonateHandler.donate(this.getInventory());
        super.close(player);
    }
}
