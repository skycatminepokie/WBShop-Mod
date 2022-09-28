package skycat.wbshop.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class WBShopAbstracter {
    public static Text textOf(String str) {
        return Text.of(str);
    }

    public static String textToJson(Text text) {
        return Text.Serializer.toJson(text);
    }


    /**
     * Send a message to a player
     * @param player
     * @param text
     * @param actionBar true to show in action bar, false to show in chat
     */
    public static void sendMessageToPlayer(ServerPlayerEntity player, Text text, boolean actionBar) {
        player.sendMessage(text, actionBar);
    }

    /**
     * Send a chat message to a player
     * @param player
     * @param text
     */
    public static void sendMessageToPlayer(ServerPlayerEntity player, Text text) {
        sendMessageToPlayer(player, text, false);
    }
}
