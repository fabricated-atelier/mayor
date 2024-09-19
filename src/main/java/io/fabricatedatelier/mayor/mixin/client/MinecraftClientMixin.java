package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.MayorKeyBindings;
import io.fabricatedatelier.mayor.util.KeyHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique
    private int majorKeyBindTicks = 0;

    @Inject(method = "handleInputEvents", at = @At("HEAD"), cancellable = true)
    private void handleInputEventsMixin(CallbackInfo info) {
        if (this.player != null && ((MayorManagerAccess) this.player).getMayorManager().isInMajorView()) {
            if (this.majorKeyBindTicks > 0) {
                this.majorKeyBindTicks--;
                info.cancel();
            } else if (MayorKeyBindings.rotateLeft.isPressed()) {
                KeyHelper.rotateKey((MinecraftClient) (Object) this, true);
                this.majorKeyBindTicks = 5;
                info.cancel();
            } else if (MayorKeyBindings.rotateRight.isPressed()) {
                KeyHelper.rotateKey((MinecraftClient) (Object) this, false);
                this.majorKeyBindTicks = 5;
                info.cancel();
            }
        }
    }

}
