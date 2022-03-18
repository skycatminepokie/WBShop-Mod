package skycat.wbshop.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;
import java.util.List;

@Environment(EnvType.SERVER)
public class DonationManager {
    public static HashMap<Item, Integer> pointValues;

    public static void addHandler(ScreenHandler handler) {
        // TODO
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
        // DEBUG:
        // System.out.println("Closing handler with syncId " + handler.syncId); // Looks like we won't have to worry about managing syncIds. That's nice.
        DefaultedList<ItemStack> stacks = handler.getStacks();
        makeDonation(stacks.subList(0, 54), player); // Get the stacks in the double chest gui (stacks 0-53)
        /*
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack itemStack = stacks.get(i);
            if (!itemStack.toString().equalsIgnoreCase("1 air")) {
                System.out.println(i + " " + itemStack);
            }
        }
        */

    }

    public static void makeDonation(List<ItemStack> itemStacks, PlayerEntity player) {
        itemStacks.iterator().forEachRemaining(itemStack -> getPointValue(itemStack));
    }

    public static int getPointValue(ItemStack itemStack) {
        // DEBUG:
        System.out.println(itemStack.getCount() + "x " + itemStack.getItem().toString());
        return itemStack.getCount() * pointValues.get(itemStack.getItem());
    }

    public static void initializePointValues() {
        // Load values from file
        // DEBUG:
        // Is an Item a representation of a kind of item, or is it an actual item that has gameplay meaning? I think it's the former.
        // Can potentially use ItemStackArgumentType to make a command to update the values
    }
}
