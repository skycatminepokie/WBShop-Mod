package skycat.wbshop.commands;

import com.google.gson.JsonElement;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.NbtTextContent;
import net.minecraft.text.Text;
import skycat.wbshop.WBShopServer;

public class WithdrawCommandHandler {

    public static int withdrawCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        // TODO: Tell the player the usage
        return 1;
    }

    public static int withdrawCalledAmount(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        NbtCompound compound = new NbtCompound();
        compound.put("wbpoints", NbtInt.of(1));
        ItemStack itemStack = new ItemStack(Items.PAPER, 1);
        itemStack.setNbt(compound);
        // itemStack.setCustomName("hi");
        // I think the second argument of putString (below) can't just be "hello" because it has to be a Text to have extra features, and a Text is stored as a String in JSON format (I think)
        itemStack.getOrCreateSubNbt("display").putString("Name", Text.Serializer.toJson(Text.of("Point Voucher")));
        NbtList lore = new NbtList();
        // OK so NbtString#of needs the JSON format of a Text in the form of a string
        lore.add(NbtString.of(Text.Serializer.toJson(Text.of("lore?"))));
        itemStack.getOrCreateSubNbt("display").put("Lore", lore);
        for (String key : itemStack.getNbt().getKeys()) {
            System.out.println(key);
        }
        context.getSource().getPlayer().getInventory().offerOrDrop(itemStack);
        return 1;
    }
}
