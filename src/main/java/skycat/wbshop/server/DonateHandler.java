package skycat.wbshop.server;

import net.minecraft.inventory.Inventory;

public class DonateHandler {
    public static void donate(Inventory inventory) {
        System.out.println("\"Donated\" inventory");
        System.out.println(inventory.getStack(0));
    }
}
