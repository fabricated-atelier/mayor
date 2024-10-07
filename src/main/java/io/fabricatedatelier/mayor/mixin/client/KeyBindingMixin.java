package io.fabricatedatelier.mayor.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.MayorKeyBind;
import io.fabricatedatelier.mayor.manager.MayorManager;
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
        MinecraftClient client = MinecraftClient.getInstance();
        if (key.getCode() == 81 || key.getCode() == 69) {
            if (client.player != null && ((MayorManagerAccess) client.player).getMayorManager().isInMajorView()) {
                if ((key.getCode() == 81 && MayorKeyBind.ROTATE_LEFT.get().isDefault()) || (key.getCode() == 69 && MayorKeyBind.ROTATE_RIGHT.get().isDefault())) {
                    info.cancel();
                }
            }
        } else if (key.getCode() == 262 || key.getCode() == 263 || key.getCode() == 264 || key.getCode() == 265) {
            MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
            if (client.player != null && mayorManager.isInMajorView() && mayorManager.getStructureOriginBlockPos() != null) {
                mayorManager.setStructureOriginBlockPos(StructureHelper.moveOrigin(mayorManager.getStructureOriginBlockPos(), key.getCode() - 262, client.player.getHorizontalFacing()));
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
                if (key.getCode() == 81 && MayorKeyBind.ROTATE_LEFT.get().isDefault()) {
                    MayorKeyBind.ROTATE_LEFT.get().setPressed(pressed);
                    info.cancel();
                } else if (key.getCode() == 69 && MayorKeyBind.ROTATE_RIGHT.get().isDefault()) {
                    MayorKeyBind.ROTATE_RIGHT.get().setPressed(pressed);
                    info.cancel();
                }
            }
        }
    }

}
