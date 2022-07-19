package skycat.wbshop.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import skycat.wbshop.server.EconomyManager;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerOnDeathMixin {
    @Inject(at = @At("HEAD"), method = "onDeath")
    public void onDeathMixin(DamageSource damageSource, CallbackInfo info) {
        EconomyManager.getInstance().onPlayerDeath(((ServerPlayerEntity)(Object)this));
    }


}
