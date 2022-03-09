package skycat.wbshop.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.OptionalInt;

@Environment(EnvType.SERVER)
public class DonateCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext context) throws CommandSyntaxException {
        if (context.getSource() instanceof ServerCommandSource) {
            ((ServerCommandSource) context.getSource()).getPlayer().openHandledScreen(
                new SimpleNamedScreenHandlerFactory(
                    (syncId, inv, player) -> GenericContainerScreenHandler.createGeneric9x6(syncId, inv, player.getInventory()), // We need to be able to access the result of createGeneric9x6 outside this
                    Text.of("GUINAME")
                )
            );

        } else {
            System.out.println("Warning: donate was (somehow) called from a non-ServerCommandSource, ignoring it.");
        }
        return 1;
    }


}
