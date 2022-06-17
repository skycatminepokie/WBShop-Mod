package skycat.wbshop.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.village.SimpleMerchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

public class TestCommandHandler {
    public static int testCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        SimpleNamedScreenHandlerFactory factory = new SimpleNamedScreenHandlerFactory((syncId, inv, player) -> {
            MerchantScreenHandler handler = new MerchantScreenHandler(syncId, player.getInventory());
            handler.setOffers(getTradeOffers());
            handler.updateToClient();
            return handler;
        }, Text.of("Shop"));
        context.getSource().getPlayer().openHandledScreen(factory);
        return 1;
    }

    private static TradeOfferList getTradeOffers() {
        TradeOfferList offerList = new TradeOfferList();
        /*
        offerList.addAll(
                // new TradeOffer(buy, sell, uses, npcXp, multiplier),
        );
        */
        offerList.add(new TradeOffer(new ItemStack(Items.PAPER), new ItemStack(Items.DIAMOND), 64, 0, 0));
        return offerList;
    }
}
