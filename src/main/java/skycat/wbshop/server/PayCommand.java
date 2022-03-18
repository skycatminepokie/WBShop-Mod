package skycat.wbshop.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public class PayCommand implements Command<ServerCommandSource> {

    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return 0;
    }
}
