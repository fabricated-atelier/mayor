package io.fabricatedatelier.mayor.camera.mode;

import io.fabricatedatelier.mayor.camera.util.CameraMode;
import io.fabricatedatelier.mayor.camera.util.CameraRotation;
import io.fabricatedatelier.mayor.camera.util.CameraTarget;
import io.fabricatedatelier.mayor.entity.custom.CameraPullEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;

public class FreeFlyMode implements CameraMode {
    private static double MOVE_SPEED = 0.05;

    private final Vec3d pos;
    private final CameraTarget targetEntity;
    private int tick = 0;


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
    public void tick(int currentTick, Consumer<Integer> tickSetter, float tickDelta) {
        this.tick = currentTick;

    }

    @Override
    public Vec3d updateCameraPos(CameraTarget target, float tickDelta) {
        Vec3d difference = target.mayor$getTargetPosition().subtract(this.pos);
        if (difference.length() <= 5) return this.pos;
        Vec3d dampedPosition = difference.multiply(MOVE_SPEED * tickDelta);
        return dampedPosition;
    }

    @Override
    public CameraRotation updateCameraRotations(CameraTarget target, Vec3d cameraPos, float tickDelta) {
        return null;
    }

    @Override
    public void handleMouseMovement(double deltaX, double deltaY) {
        CameraMode.super.handleMouseMovement(deltaX, deltaY);
        //TODO: rotate camera using deltaX only ?
    }
}
