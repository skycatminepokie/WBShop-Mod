package skycat.wbshop.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal; // Makes a LiteralArgumentBuilder from a string

public class DonateCommand implements Command<CommandContext> {
    @Override
    public int run(CommandContext<CommandContext> context) throws CommandSyntaxException {
        System.out.println("Donate command called!");
        return 0;
    }

    public static void register(CommandDispatcher dispatcher, boolean isDedicated) {
        dispatcher.register(buildCommand());
    }

    private static LiteralArgumentBuilder buildCommand() {
        // Base
        LiteralArgumentBuilder command = literal("donate");
        command.executes(new DonateCommand());

        return command;
    }
}
