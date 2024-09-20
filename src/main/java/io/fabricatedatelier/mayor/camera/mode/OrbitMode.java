package io.fabricatedatelier.mayor.camera.mode;

import io.fabricatedatelier.mayor.camera.util.CameraMode;
import io.fabricatedatelier.mayor.camera.util.CameraTarget;
import io.fabricatedatelier.mayor.camera.util.CameraRotation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;

public class OrbitMode implements CameraMode {
    public static final int FULL_ORBIT_TICK_DURATION = 8000;

    private float normalizedOrbitProgress = 0;
    private float height = 5, distance = 10;

    public float getNormalizedOrbitProgress() {
        return MathHelper.clamp(this.normalizedOrbitProgress, 0f, 1f);
    }

    public void setNormalizedOrbitProgress(float normalizedOrbitProgress) {
        this.normalizedOrbitProgress = MathHelper.clamp(normalizedOrbitProgress, 0f, 1f);
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = Math.max(height, 1);
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = Math.max(distance, 1);
    }

    private double getHorizontalDistance(CameraTarget target, Vec3d cameraPos) {
        Vec3d targetPos = target.mayor$getTargetPosition();
        return Math.sqrt(MathHelper.square((cameraPos.x - targetPos.x))
                + MathHelper.square((cameraPos.y - targetPos.y))
                + MathHelper.square((cameraPos.z - targetPos.z))
        );
    }

    @Override
    public boolean needsTarget() {
        return true;
    }

    @Override
    public void tick(int currentTick, Consumer<Integer> tickSetter) {
        float normalizedProgress = (float) currentTick / FULL_ORBIT_TICK_DURATION;
        setNormalizedOrbitProgress(normalizedProgress);
        if (currentTick >= FULL_ORBIT_TICK_DURATION) {
            tickSetter.accept(0);
        }
    }

    @Override
    public Vec3d updateCameraPos(CameraTarget target) {
        float progressAngle = MathHelper.lerp(this.getNormalizedOrbitProgress(), 0f, 360f);
        Vec3d targetPos = target.mayor$getTargetPosition();
        double x = targetPos.x + this.getDistance() * Math.cos(Math.toRadians(progressAngle));
        double y = targetPos.y + this.getHeight();
        double z = targetPos.z + this.getDistance() * Math.sin(Math.toRadians(progressAngle));
        return new Vec3d(x, y, z);
    }

    @Override
    public CameraRotation updateCameraRotations(CameraTarget target, Vec3d cameraPos) {
        double deltaX = target.mayor$getTargetPosition().x - cameraPos.x;
        double deltaZ = target.mayor$getTargetPosition().z - cameraPos.z;
        double pitchInRad = Math.asin(getHeight() / getHorizontalDistance(target, cameraPos));
        double yawInRad = MathHelper.atan2(deltaZ, deltaX);
        return new CameraRotation(pitchInRad, yawInRad);
    }

    @Override
    public void handleScroll(double delta) {
        this.setDistance((float) Math.max(this.getDistance() + delta, 1));
        this.setHeight((float) Math.max(this.getHeight() + delta, 0));
    }
}
