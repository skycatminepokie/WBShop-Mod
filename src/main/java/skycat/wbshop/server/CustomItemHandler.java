package skycat.wbshop.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Random;

@Environment(EnvType.SERVER)
public class CustomItemHandler implements UseItemCallback {
    private static final String[] CUSTOM_ITEM_IDS = {"fireBook"};
    private static final String ITEM_ID_NBT_KEY = "wbshopItemId"; // The key to store custom item ids under
    private static final double FIRE_BOOK_LAUNCH_SPEED = 3.0;
    private static final double FIRE_BOOK_LAUNCH_ERROR = 0.9; // Max possible error for rotation on each axis, exclusive

    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        if (!(world instanceof ServerWorld)) {
            return TypedActionResult.pass(ItemStack.EMPTY);
        }
        ItemStack itemStack = player.getStackInHand(hand);
        if (isCustomItem(itemStack)) { // If the used item is a custom item
            NbtCompound itemNbt = itemStack.getNbt();
            if (itemNbt.get(ITEM_ID_NBT_KEY).asString().equals("fireBook")) {
                PlayerInventory inventory = player.getInventory();
                if (inventory.containsAny(Collections.singleton(Items.FIRE_CHARGE))) {
                    for (int i = 0; i <= inventory.size(); i++ ) {
                        ItemStack stack = inventory.getStack(i);
                        if (stack.getItem().equals(Items.FIRE_CHARGE)) {
                            stack.decrement(1);
                            break;
                        }
                    }
                    Vec3d rotation = player.getRotationVec(1);
                    Random random = new Random();
                    SmallFireballEntity fireball = new SmallFireballEntity(world, player.getX(), player.getEyeY(), player.getZ(), rotation.x * (1 + random.nextDouble(FIRE_BOOK_LAUNCH_ERROR)), rotation.y * (1 + random.nextDouble(FIRE_BOOK_LAUNCH_ERROR)), rotation.z * (1 + random.nextDouble(FIRE_BOOK_LAUNCH_ERROR)));
                    fireball.powerX *= FIRE_BOOK_LAUNCH_SPEED;
                    fireball.powerY *= FIRE_BOOK_LAUNCH_SPEED;
                    fireball.powerZ *= FIRE_BOOK_LAUNCH_SPEED;
                    world.spawnEntity(fireball);
                    // TODO: Mixin so I can use fireball.playUseSound() maybe
                    world.playSound(player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, random.nextFloat(0.5f, 1.001f), false); // don't know what useDistance does, but oh well. Used somewhat random pitch because FireChargeItem does, and it sounds nicer // WARN Do I need to credit that?
                    return TypedActionResult.success(ItemStack.EMPTY);
                }
                // EntityType.PIG.spawn((ServerWorld) world, null, null, null, new BlockPos(player.getPos()), SpawnReason.COMMAND, true, false);
            }

        }
        return TypedActionResult.pass(ItemStack.EMPTY); // Not sure why we give it ItemStack.EMPTY, but UseItemCallback.EVENT does it, so I guess that's what I should do xd
    }

    private boolean isCustomItem(ItemStack itemStack) { // TODO Probably could be simplified by catching errors instead of error-proofing
        NbtCompound stackNbt = itemStack.getNbt();
        if (stackNbt != null && stackNbt.contains(ITEM_ID_NBT_KEY)) { // If the custom nbt is not null and contains the key denoting one of our custom items
            NbtElement nbtElement = stackNbt.get(ITEM_ID_NBT_KEY);
            if (nbtElement != null) { // And the element at the key is not null
                NbtType<?> type = nbtElement.getNbtType(); // Don't know why we need <?>, but intellij yells at me otherwise
                if (type != null && type.equals(NbtString.TYPE)) { // And the element is a string
                    String nbtValue = nbtElement.asString();
                    for (String itemId : CUSTOM_ITEM_IDS) { // If the string matches the id for one of our custom items (if it's valid)
                        if (itemId.equals(nbtValue)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
