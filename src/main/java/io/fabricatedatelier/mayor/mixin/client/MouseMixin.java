package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.util.CameraHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"), cancellable = true)
    private void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        GameOptions options = client.options;
        ClientPlayerEntity player = client.player;
        boolean mouseScrolled = options.getDiscreteMouseScroll().getValue();
        double delta = (mouseScrolled ? Math.signum(horizontal) : vertical) * options.getMouseWheelSensitivity().getValue();

        if (player == null) return;
        if (!CameraHelper.getInstance().hasTarget()) return;
        CameraHelper.getInstance().handleScroll(delta);
        ci.cancel();
    }
}
