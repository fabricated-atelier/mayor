package io.fabricatedatelier.mayor.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    // fixes: moved wrongly! issue when in survival mode and using entity view via mayor view
    @ModifyExpressionValue(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;isCreative()Z"))
    private boolean onPlayerMoveMixin(boolean original) {
        if (!original && ((MayorManagerAccess) player).getMayorManager().isInMajorView()) {
            return true;
        }
        return original;
    }
}
