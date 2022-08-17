package skycat.wbshop.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import skycat.wbshop.WBShopServer;

import java.util.HashMap;
import java.util.List;

@Environment(EnvType.SERVER)
public class DonationManager {
    public static HashMap<Item, Integer> pointValues;

    public static void addHandler(ScreenHandler handler) {
        // This is how we managed to get the handler out of the method call in the main mod class. Hopefully we can manipulate it from here.
        // System.out.println(handler.getStacks().get(1));
        /*
        handler.addListener(new ScreenHandlerListener() {
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
                // Debug
                for (ItemStack itemStack : handler.getStacks()) {
                    if (!itemStack.toString().equalsIgnoreCase("1 air")) {
                        System.out.println(itemStack.toString());

                    }
                }
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

            }
        });
        */
    }

    public static void donateScreenClosing(ScreenHandler handler, PlayerEntity player) {
        DefaultedList<ItemStack> stacks = handler.getStacks();
        makeDonation(stacks.subList(0, 54), player); // Get the stacks in the double chest gui (stacks 0-53)
    }

    public static int getPointValue(ItemStack itemStack) {
        NbtCompound compound = itemStack.getNbt();
        if (compound != null) { // If it has custom NBT
            NbtInt pointsNbt = (NbtInt) compound.get("wbpoints"); // Is there a better name than pointsNbt? I want it to note that it is in an nbt form.
            if (pointsNbt != null) { // And it has wbpoints stored in its nbt
                return pointsNbt.intValue() * itemStack.getCount(); // Then give credit for the value stored in the voucher
            }
        }

        return itemStack.getCount(); // For now, every item will be worth exactly 1 point.
    }

    public static void reloadPointValues() {
        // Load values from file
        // Can potentially use ItemStackArgumentType to make a command to update the values
    }

    public static void makeDonation(List<ItemStack> itemStacks, PlayerEntity player) {
        itemStacks.iterator().forEachRemaining(itemStack -> EconomyManager.getInstance().addBalance(player.getUuid(), getPointValue(itemStack)));
        WorldBorderHelper.updateWorldBorder(WBShopServer.ECONOMY_MANAGER);
    }
}
