package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.MayorKeyBind;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressedMixin(InputUtil.Key key, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();

        if (key.getCode() == InputUtil.GLFW_KEY_Q || key.getCode() == InputUtil.GLFW_KEY_E) {
            if (mayorManager.isInMajorView()) {
                if (key.getCode() == InputUtil.GLFW_KEY_Q && MayorKeyBind.ROTATE_LEFT.get().isDefault()) {
                    info.cancel();
                }
                if (key.getCode() == InputUtil.GLFW_KEY_E && MayorKeyBind.ROTATE_RIGHT.get().isDefault()) {
                    info.cancel();
                }
            }
            return;
        }
        if (key.getCode() == InputUtil.GLFW_KEY_RIGHT ||
                key.getCode() == InputUtil.GLFW_KEY_LEFT ||
                key.getCode() == InputUtil.GLFW_KEY_DOWN ||
                key.getCode() == InputUtil.GLFW_KEY_UP) {
            if (!mayorManager.isInMajorView() || mayorManager.getStructureOriginBlockPos() == null) return;
            mayorManager.setStructureOriginBlockPos(StructureHelper.moveOrigin(mayorManager.getStructureOriginBlockPos(),
                    key.getCode() - 262, client.player.getHorizontalFacing()));

            // ClientPlayNetworking.send(new StructureOriginPacket(Optional.of(StructureHelper.moveOrigin(mayorManager.getOriginBlockPos(), key.getCode() - 263))));
            // Maybe sync origin to server - nope or maybe
        }
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressedMixin(InputUtil.Key key, boolean pressed, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();

        if (mayorManager.isInMajorView()) {
            if (key.getCode() == InputUtil.GLFW_KEY_Q && MayorKeyBind.ROTATE_LEFT.get().isDefault()) {
                MayorKeyBind.ROTATE_LEFT.get().setPressed(pressed);
                info.cancel();
            } else if (key.getCode() == InputUtil.GLFW_KEY_E && MayorKeyBind.ROTATE_RIGHT.get().isDefault()) {
                MayorKeyBind.ROTATE_RIGHT.get().setPressed(pressed);
                info.cancel();
            }
        }
    }

}
