package skycat.wbshop.mixin;

import net.minecraft.screen.MerchantScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreenHandler.class)
public class MerchantScreenHandlerPlaySoundMixin {
    @Inject(at = @At("HEAD"), method = "playYesSound", cancellable = true)
    private void cancelPlayYesSound(CallbackInfo ci) {
        ci.cancel();
    }
}
