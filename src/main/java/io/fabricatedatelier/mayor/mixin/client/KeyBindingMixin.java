package io.fabricatedatelier.mayor.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.util.MayorManager;
import io.fabricatedatelier.mayor.util.StructureHelper;
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
        if (key.getCode() == 81 || key.getCode() == 69) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && ((MayorManagerAccess) client.player).getMayorManager().isInMajorView()) {
                if ((key.getCode() == 81 && KeyBindings.majorRotateLeftKeyBind.isDefault()) || (key.getCode() == 69 && KeyBindings.majorRotateRightKeyBind.isDefault())) {
                    info.cancel();
                }
            }
        } else if (key.getCode() == 262 || key.getCode() == 263 || key.getCode() == 264 || key.getCode() == 265) {
            MinecraftClient client = MinecraftClient.getInstance();
            MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
            if (client.player != null && mayorManager.isInMajorView() && mayorManager.getOriginBlockPos() != null) {
                mayorManager.setOriginBlockPos(StructureHelper.moveOrigin(mayorManager.getOriginBlockPos(), key.getCode() - 262, client.player.getHorizontalFacing()));
                // ClientPlayNetworking.send(new StructureOriginPacket(Optional.of(StructureHelper.moveOrigin(mayorManager.getOriginBlockPos(), key.getCode() - 263))));
                // Maybe sync origin to server - nope or maybe
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
                } else if (key.getCode() == 69 && KeyBindings.majorRotateRightKeyBind.isDefault()) {
                    KeyBindings.majorRotateRightKeyBind.setPressed(pressed);
                    info.cancel();
                }
            }
        }
    }

}
