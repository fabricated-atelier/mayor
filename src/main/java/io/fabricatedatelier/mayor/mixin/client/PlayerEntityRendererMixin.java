package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "setModelPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isSpectator()Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setModelPoseMixin(AbstractClientPlayerEntity player, CallbackInfo info, PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel) {
        if (((MayorManagerAccess) MinecraftClient.getInstance().player).getMayorManager().isInMajorView()) {
            playerEntityModel.setVisible(false);
            playerEntityModel.head.visible = false;
            playerEntityModel.hat.visible = false;
            info.cancel();
        }

    }
}
