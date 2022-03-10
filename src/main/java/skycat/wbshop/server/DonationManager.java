package skycat.wbshop.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

@Environment(EnvType.SERVER)
public class DonationManager {
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
    public static void donateScreenClosing(ScreenHandler handler, PlayerEntity player) { // OK so the only stacks we care about are 0-53. The rest are in the player's inventory
        System.out.println("Closing handler with syncId " + handler.syncId); // Looks like we won't have to worry about managing these.
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
        itemStacks.iterator().forEachRemaining(itemStack -> System.out.println(itemStack.toString()));
    }
}
