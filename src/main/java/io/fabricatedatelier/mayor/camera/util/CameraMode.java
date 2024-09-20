package io.fabricatedatelier.mayor.camera.util;

import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;

public interface CameraMode {
    boolean needsTarget();

    void tick(int currentTick, Consumer<Integer> tickSetter);

    Vec3d updateCameraPos(CameraTarget target);

    CameraRotation updateCameraRotations(CameraTarget target, Vec3d cameraPos);

    default void handleScroll(double delta) {
    }

    default void handleMouseMovement(double deltaX, double deltaY) {
    }

}
