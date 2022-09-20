package skycat.wbshop.util;

import net.minecraft.text.Text;

public class WBShopAbstracter {
    public static Text textOf(String str) {
        return Text.of(str);
    }

    public static String textToJson(Text text) {
        return Text.Serializer.toJson(text);
    }
}
