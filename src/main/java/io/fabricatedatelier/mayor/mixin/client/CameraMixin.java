package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.camera.CameraHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    protected abstract void setPos(Vec3d pos);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    private void cameraOrbiting(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (focusedEntity.getWorld() == null) {
            CameraHandler.getInstance().end();
            return;
        }
        if (!(focusedEntity instanceof ClientPlayerEntity clientPlayer)) return;
        CameraHandler camera = CameraHandler.getInstance();

        if (camera.getTarget().isEmpty()) {
            return;
        }
        if (clientPlayer.isSneaking()) {
            camera.end();
        }

        camera.getMode().ifPresent(cameraMode -> {
            if (cameraMode.needsTarget() && camera.getTarget().isEmpty()) return;
            camera.tick();
            this.setPos(camera.getCameraPos());
            this.setRotation((float) camera.getYaw(), (float) camera.getPitch());
        });
    }
}
