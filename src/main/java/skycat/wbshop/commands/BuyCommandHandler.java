package skycat.wbshop.commands;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.NbtTextContent;
import skycat.wbshop.WBShopServer;
import skycat.wbshop.server.CustomItemHandler;

import static skycat.wbshop.WBShopServer.LOGGER;

public class BuyCommandHandler {
    public static int buyCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException { // TODO: Better method name, only works with a string arg
        /*if (context.getSource().isExecutedByPlayer()) {
            context.getSource().getPlayer().getInventory().offerOrDrop(CustomItemHandler.CUSTOM_ITEMS.get("fireBook").get());
            String itemString = context.getArgument("itemName", StringArgumentType.string().getClass()).toString();
            if (CustomItemHandler.CUSTOM_ITEMS.containsKey(itemString)) {
                // TODO If allowed to buy
                context.getSource().getPlayer().getInventory().offerOrDrop(CustomItemHandler.CUSTOM_ITEMS.get(itemString).get());
            } else {
                throw new SimpleCommandExceptionType(new LiteralMessage("Invalid item")).create();
            }
            return 1;
        } else {
            throw new SimpleCommandExceptionType(new LiteralMessage("This command must be executed by a player")).create(); // TODO Should I be using SimpleCommandExceptionType as a builder class?
        }*/
        LOGGER.info("nbt in hand: " + context.getSource().getPlayer().getMainHandStack().getNbt().toString());
        context.getSource().getPlayer().getInventory().offerOrDrop(ItemStack.fromNbt(NbtHelper.fromNbtProviderString(
                "{id:\"minecraft:book\",Count:1b,tag:{display:{Name:'{\"text\":\"Fire Book\",\"color\":\"red\",\"bold\":true,\"italic\":true}',Lore:['{\"text\":\"Shoots fire. Like, a lot of it.\"}','{\"text\":\" \"}','[{\"text\":\"Uses \",\"font\":\"uniform\",\"color\":\"light_purple\",\"italic\":true},{\"translate\":\"item.minecraft.fire_charge\",\"font\":\"uniform\",\"color\":\"gold\"},{\"text\":\"s\",\"font\":\"uniform\",\"color\":\"gold\"},{\"text\":\" as ammo.\",\"font\":\"uniform\",\"color\":\"light_purple\",\"italic\":true}]','{\"text\":\" \"}','{\"text\":\"\\\\\"You should work on your aim!\\\\\"\",\"color\":\"dark_green\",\"italic\":true}','{\"text\":\"/credits PyroStunts\",\"color\":\"blue\",\"underlined\":true}']},wbshopItemId:\"fireBook\"}}"
        ))); // CREDIT PyroStunts
        // TODO: /credit command
        return 1;
    }
}
