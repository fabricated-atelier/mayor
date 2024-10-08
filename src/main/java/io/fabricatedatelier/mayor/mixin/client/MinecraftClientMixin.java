package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.MayorClientEvents;
import io.fabricatedatelier.mayor.init.MayorKeyBind;
import io.fabricatedatelier.mayor.util.KeyHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Mutable
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Mutable
    @Final
    private Window window;

    @Unique
    private int majorKeyBindTicks = 0;

    @Inject(method = "handleInputEvents", at = @At("HEAD"), cancellable = true)
    private void handleInputEventsMixin(CallbackInfo info) {
        if (this.player != null && ((MayorManagerAccess) this.player).getMayorManager().isInMajorView()) {
            if (this.majorKeyBindTicks > 0) {
                this.majorKeyBindTicks--;
                info.cancel();
            } else if (MayorKeyBind.ROTATE_LEFT.get().isPressed()) {
                KeyHelper.rotateKey((MinecraftClient) (Object) this, true);
                this.majorKeyBindTicks = 5;
                info.cancel();
            } else if (MayorKeyBind.ROTATE_RIGHT.get().isPressed()) {
                KeyHelper.rotateKey((MinecraftClient) (Object) this, false);
                this.majorKeyBindTicks = 5;
                info.cancel();
            }
        }
    }

    @Inject(method = "onResolutionChanged", at = @At("TAIL"))
    private void onResolutionChangedMixin(CallbackInfo info) {
        MayorClientEvents.ALPHA_FRAMEBUFFER.get().resize(window.getFramebufferWidth(), window.getFramebufferHeight(), MinecraftClient.IS_SYSTEM_MAC);
    }

}
