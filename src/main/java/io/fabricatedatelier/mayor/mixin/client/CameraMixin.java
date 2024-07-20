package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.util.CameraHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    public abstract Vec3d getPos();

    @Shadow
    protected abstract void setPos(Vec3d pos);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Unique private int tick = 0;

    @Inject(method = "update", at = @At("TAIL"))
    private void test(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (focusedEntity.getWorld() == null) {
            CameraHelper.getInstance().setTarget(null);
            return;
        }
        if (!(focusedEntity instanceof ClientPlayerEntity clientPlayer)) return;
        CameraHelper camera = CameraHelper.getInstance();
        camera.getTarget().ifPresent(target -> {
            tick++;
            float normalizedProgress = (float) tick/ CameraHelper.FULL_ORBIT_TICK_DURATION;
            Mayor.LOGGER.info(String.valueOf(normalizedProgress));
            camera.setNormalizedOrbitProgress(normalizedProgress);
            camera.updateCameraPos();
            camera.updateCameraRotations();
            this.setPos(camera.getCameraPos());
            this.setRotation((float) camera.getYaw(), (float) camera.getPitch());
            if (this.tick >= CameraHelper.FULL_ORBIT_TICK_DURATION) this.tick = 0;
        });
    }
}
