package io.fabricatedatelier.mayor.camera.mode;

import io.fabricatedatelier.mayor.camera.util.CameraMode;
import io.fabricatedatelier.mayor.camera.util.CameraRotation;
import io.fabricatedatelier.mayor.camera.util.CameraTarget;
import io.fabricatedatelier.mayor.entity.custom.CameraPullEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;

public class FreeFlyMode implements CameraMode {
    private final Vec3d pos;
    private final CameraTarget targetEntity;

    public FreeFlyMode(CameraPullEntity pullEntity, Vec3d startPos) {
        this.pos = startPos;
        this.targetEntity = pullEntity;
    }

    public FreeFlyMode(CameraPullEntity pullEntity) {
        this(pullEntity, pullEntity.getPos());
    }

    @Override
    public boolean needsTarget() {
        return true;
    }

    @Override
    public void tick(int currentTick, Consumer<Integer> tickSetter) {

    }

    @Override
    public Vec3d updateCameraPos(CameraTarget target) {
        return null;
    }

    @Override
    public CameraRotation updateCameraRotations(CameraTarget target, Vec3d cameraPos) {
        return null;
    }

    private static Vec3d interpolatePosition(float progress, Vec3d start, Vec3d end) {
        MathHelper.lerp2()
    }
}
