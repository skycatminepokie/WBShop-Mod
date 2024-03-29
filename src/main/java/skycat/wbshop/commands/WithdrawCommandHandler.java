package skycat.wbshop.commands;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import skycat.wbshop.server.EconomyManager;
import skycat.wbshop.server.WorldBorderHelper;

import static skycat.wbshop.util.WBShopAbstracter.textOf;
import static skycat.wbshop.util.WBShopAbstracter.textToJson;

public class WithdrawCommandHandler {

    public static int withdrawCalled(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(textOf("Use /withdraw [amount] to turn your points into vouchers. Vouchers do not count towards the world border."), false);
        return 1;
    }

    public static int withdrawCalledAmount(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int amount = context.getArgument("amount", int.class);
        ServerPlayerEntity thePlayer = context.getSource().getPlayer();
        return withdrawToVoucher(thePlayer, amount);
    }

    public static int withdrawCalledAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity thePlayer = context.getSource().getPlayer();
        int playerBal = EconomyManager.getInstance().getBalance(thePlayer);
        if (playerBal > 0) {
            return withdrawToVoucher(thePlayer, playerBal);
        } else {
            throw new SimpleCommandExceptionType(new LiteralMessage("You need at least 1 point to withdraw.")).create();
        }
    }

    private static int withdrawToVoucher(ServerPlayerEntity thePlayer, int amount) throws CommandSyntaxException {
        if (EconomyManager.getInstance().getBalance(thePlayer.getUuid()) < amount) {
            throw new SimpleCommandExceptionType(new LiteralMessage("You don't have enough points for that!")).create();
        }

        // Make the ItemStack
        ItemStack itemStack = new ItemStack(Items.PAPER, 1);

        // Add custom NBT
        NbtCompound compound = new NbtCompound();
        compound.put("wbpoints", NbtInt.of(amount));
        itemStack.setNbt(compound);

        // Add vanilla NBT
        // Set name
        itemStack.setCustomName(textOf("Point Voucher"));

        // Prep lore
        NbtList lore = new NbtList();
        // NbtString#of needs the JSON format of a Text in the form of a string
        lore.add(NbtString.of(textToJson(textOf(amount + " point" + (amount == 1 ? "" : "s")))));

        // Set lore
        itemStack.getOrCreateSubNbt("display").put("Lore", lore);

        // Give the player the item (or drop it on the ground if they don't have inventory space)
        thePlayer.getInventory().offerOrDrop(itemStack);

        // Remove the withdrawn points from the player's wallet
        EconomyManager.getInstance().removeBalance(thePlayer.getUuid(), amount);

        // Update the world border
        WorldBorderHelper.updateWorldBorder(EconomyManager.getInstance());

        return 1;
    }
}
