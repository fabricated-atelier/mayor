package io.fabricatedatelier.mayor.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.KeyBindings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressedMixin(InputUtil.Key key, CallbackInfo info) {
        System.out.println(key.getCode());
        if (key.getCode() == 81 || key.getCode() == 69) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && ((MayorManagerAccess) client.player).getMayorManager().isInMajorView()) {
                if ((key.getCode() == 81 && KeyBindings.majorRotateLeftKeyBind.isDefault()) || (key.getCode() == 69 && KeyBindings.majorRotateRightKeyBind.isDefault())) {
                    info.cancel();
                }
            }
        }
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressedMixin(InputUtil.Key key, boolean pressed, CallbackInfo info) {
        if (key.getCode() == 81 || key.getCode() == 69) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && ((MayorManagerAccess) client.player).getMayorManager().isInMajorView()) {
                if (key.getCode() == 81 && KeyBindings.majorRotateLeftKeyBind.isDefault()) {
                    KeyBindings.majorRotateLeftKeyBind.setPressed(pressed);
                    info.cancel();
                }else  if (key.getCode() == 69 && KeyBindings.majorRotateRightKeyBind.isDefault()) {
                    KeyBindings.majorRotateRightKeyBind.setPressed(pressed);
                    info.cancel();
                }
            }
        }
    }

}
