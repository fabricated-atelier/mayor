package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.Mayor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Singleton Helper class to register none or one specific Camera location and angle.<br><br>
 *
 * This camera helper makes use of an Orbit around a specific location, which is defined by the
 * {@link CameraTarget} Interface. This interface is implemented for
 * {@link io.fabricatedatelier.mayor.mixin.EntityMixin Entities} and
 * {@link io.fabricatedatelier.mayor.mixin.BlockEntityMixin BlockEntities} by default.
 * @implNote Use {@link CameraHelper#getInstance()} to get access to the object of the CameraHelper
 */
@SuppressWarnings("UnusedReturnValue")
public class CameraHelper {
    public static final int FULL_ORBIT_TICK_DURATION = 100;

    private static CameraHelper instance;

    @Nullable
    private CameraTarget target;
    private Vec3d cameraPos = Vec3d.ZERO;
    private double pitch, yaw;

    private float normalizedOrbitProgress = 0;
    private float height = 5, distance = 10;
    private int tick = 0;

    private CameraHelper() {
    }

    public static CameraHelper getInstance() {
        if (instance == null) instance = new CameraHelper();
        return instance;
    }


    public Optional<CameraTarget> getTarget() {
        return Optional.ofNullable(target);
    }

    public boolean hasTarget() {
        return getTarget().isPresent();
    }

    public CameraHelper setTarget(@Nullable CameraTarget target) {
        this.target = target;
        return this;
    }

    public double getHeight() {
        return height;
    }

    public CameraHelper setHeight(float height) {
        this.height = height;
        return this;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Vec3d getCameraPos() {
        return cameraPos;
    }

    private CameraHelper setCameraPos(Vec3d cameraPos) {
        this.cameraPos = cameraPos;
        return this;
    }

    public double getPitch() {
        return pitch;
    }

    private CameraHelper setPitch(double pitch) {
        this.pitch = pitch;
        return this;
    }

    public double getYaw() {
        return yaw;
    }

    private CameraHelper setYaw(double yaw) {
        this.yaw = yaw;
        return this;
    }

    public float getNormalizedOrbitProgress() {
        return MathHelper.clamp(this.normalizedOrbitProgress, 0f, 1f);
    }

    public void setNormalizedOrbitProgress(float normalizedOrbitProgress) {
        this.normalizedOrbitProgress = MathHelper.clamp(normalizedOrbitProgress, 0f, 1f);
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public void updateCameraPos() {
        if (this.getTarget().isEmpty()) return;
        float progressAngle = MathHelper.lerp(this.getNormalizedOrbitProgress(), 0f, 360f);

        Vec3d targetPos = this.getTarget().get().mayor$getTargetPosition();
        double x = targetPos.x + this.distance * Math.cos(Math.toRadians(progressAngle));
        double y = targetPos.y + this.height;
        double z = targetPos.z + this.distance * Math.sin(Math.toRadians(progressAngle));
        this.setCameraPos(new Vec3d(x, y, z));
    }

    public void updateCameraRotations() {
        if (this.getTarget().isEmpty()) return;
        this.setPitch(Math.atan(getHeight()) / getDistance());
        this.setYaw(MathHelper.atan2(getCameraPos().z, getCameraPos().x));
        Mayor.LOGGER.info("{} | {}", getPitch(), getYaw());
    }
}
