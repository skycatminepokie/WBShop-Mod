package skycat.wbshop.server;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOfferList;

public class ShopScreenHandler extends MerchantScreenHandler {

    public ShopScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(syncId, playerInventory);
    }


}
