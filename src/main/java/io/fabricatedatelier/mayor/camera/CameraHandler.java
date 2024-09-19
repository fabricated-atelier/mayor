package io.fabricatedatelier.mayor.camera;

import io.fabricatedatelier.mayor.camera.target.CameraTarget;
import io.fabricatedatelier.mayor.camera.target.StaticCameraTarget;
import io.fabricatedatelier.mayor.camera.transition.FadeTransition;
import io.fabricatedatelier.mayor.util.TransitionState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * <h1>CameraHandler</h1>
 * Singleton class for registering none or one specific Camera location and angle.<br><br>
 * <p>
 * This camera handler makes use of an Orbit around a specific location, which is defined by the
 * {@link CameraTarget} Interface. This interface is implemented for
 * {@link io.fabricatedatelier.mayor.mixin.EntityMixin MayorEntities} and
 * {@link io.fabricatedatelier.mayor.mixin.BlockEntityMixin MayorBlockEntities} by default.
 * If you don't have access to those kind of objects, you can create a new instance of
 * {@link StaticCameraTarget StaticCameraTarget} and use this as a {@link CameraTarget}.
 * This will always stay on the specified target location.
 *
 * @implNote <ul>
 * <li>Use {@link CameraHandler#getInstance()} to get access to the object of the CameraHandler on the client side.</li>
 * <li>To reset or disable the {@link CameraHandler} instance, pass in <code>null</code> into {@link #setTarget(CameraTarget) setTarget()}</li>
 * </ul>
 */
@SuppressWarnings("UnusedReturnValue")
public class CameraHandler {
    public static final int FULL_ORBIT_TICK_DURATION = 8000;
    public static final int TICKS_UNTIL_IDLE_FINISHED = 800;

    private static CameraHandler instance;

    @Nullable
    private CameraTarget target;
    private Vec3d cameraPos = Vec3d.ZERO;
    private double pitch, yaw;

    private float normalizedOrbitProgress = 0;
    private float height = 5, distance = 10;
    private int ticksSinceLastInteraction = 0;
    private int tick = ticksSinceLastInteraction;

    private final FadeTransition startFadeTransition, endFadeTransition;

    private CameraHandler(FadeTransition startFadeTransition, FadeTransition endFadeTransition) {
        this.startFadeTransition = startFadeTransition;
        this.endFadeTransition = endFadeTransition;

        this.startFadeTransition.startTicking();
    }

    public static CameraHandler getInstance() {
        if (instance == null) instance = new CameraHandler(
                new FadeTransition(MinecraftClient.getInstance(), 200, TransitionState.STARTING),
                new FadeTransition(MinecraftClient.getInstance(), 100, TransitionState.ENDING)
        );
        return instance;
    }

    public Optional<CameraTarget> getTarget() {
        return Optional.ofNullable(target);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasTarget() {
        return getTarget().isPresent();
    }

    public CameraHandler setTarget(@Nullable CameraTarget target) {
        if (target == null) {
            this.getEndTransition().startTicking();
            return this;
        }
        this.target = target;
        return this;
    }

    public void end() {
        setTarget(null);
    }

    public double getHeight() {
        return height;
    }

    public CameraHandler setHeight(float height) {
        this.height = Math.max(height, 1);
        return this;
    }

    public float getDistance() {
        return distance;
    }

    public CameraHandler setDistance(float distance) {
        this.distance = Math.max(distance, 1);
        return this;
    }

    public FadeTransition getStartTransition() {
        return startFadeTransition;
    }

    public FadeTransition getEndTransition() {
        return endFadeTransition;
    }

    public boolean isFinished() {
        return getEndTransition().isFinished();
    }

    private double getHorizontalDistance() {
        if (this.getTarget().isEmpty()) return 0;

        Vec3d targetPos = this.getTarget().get().mayor$getTargetPosition();
        Vec3d camPos = this.getCameraPos();
        return Math.sqrt(MathHelper.square((camPos.x - targetPos.x))
                + MathHelper.square((camPos.y - targetPos.y))
                + MathHelper.square((camPos.z - targetPos.z))
        );
    }

    public Vec3d getCameraPos() {
        return cameraPos;
    }

    private CameraHandler setCameraPos(Vec3d cameraPos) {
        this.cameraPos = cameraPos;
        return this;
    }

    public double getPitch() {
        return pitch;
    }

    private CameraHandler setPitch(double pitch) {
        this.pitch = pitch;
        return this;
    }

    public double getYaw() {
        return yaw;
    }

    private CameraHandler setYaw(double yaw) {
        this.yaw = yaw;
        return this;
    }

    public float getNormalizedOrbitProgress() {
        return MathHelper.clamp(this.normalizedOrbitProgress, 0f, 1f);
    }

    public CameraHandler setNormalizedOrbitProgress(float normalizedOrbitProgress) {
        this.normalizedOrbitProgress = MathHelper.clamp(normalizedOrbitProgress, 0f, 1f);
        return this;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public int getTicksSinceLastInteraction() {
        return this.ticksSinceLastInteraction;
    }

    public void setTicksSinceLastInteraction(int ticksSinceLastInteraction) {
        this.ticksSinceLastInteraction = ticksSinceLastInteraction;
    }


    public void tick() {
        if (isFinished()) {
            instance = null;
            return;
        }
        this.setTicksSinceLastInteraction(this.getTicksSinceLastInteraction() + 1);
        if (this.getTicksSinceLastInteraction() >= TICKS_UNTIL_IDLE_FINISHED) {
            this.setTick(this.getTick() + 1);
        }

        if (!hasTarget()) return;
        if (getStartTransition().isRunning()) {
            getStartTransition().tick();
        }
        if (getEndTransition().isRunning()) {
            getEndTransition().tick();
        }

        if (getEndTransition().isFinished()) {
            CameraHandler.instance = null;
        }
    }

    public void updateCameraPos() {
        if (this.getTarget().isEmpty()) return;

        float progressAngle = MathHelper.lerp(this.getNormalizedOrbitProgress(), 0f, 360f);
        Vec3d targetPos = this.getTarget().get().mayor$getTargetPosition();
        double x = targetPos.x + this.getDistance() * Math.cos(Math.toRadians(progressAngle));
        double y = targetPos.y + this.getHeight();
        double z = targetPos.z + this.getDistance() * Math.sin(Math.toRadians(progressAngle));
        this.setCameraPos(new Vec3d(x, y, z));
    }

    public void updateCameraRotations() {
        if (this.getTarget().isEmpty() || this.getTarget().get().mayor$getTargetPosition() == null) return;

        double pitchInRad = Math.asin(getHeight() / getHorizontalDistance());
        this.setPitch(Math.toDegrees(pitchInRad));
        double deltaX = getTarget().get().mayor$getTargetPosition().x - this.getCameraPos().x;
        double deltaZ = getTarget().get().mayor$getTargetPosition().z - this.getCameraPos().z;
        double yawInRad = MathHelper.atan2(deltaZ, deltaX);
        this.setYaw(Math.toDegrees(yawInRad) - 90);
    }

    public void handleScroll(double delta) {
        this.setDistance((float) Math.max(this.getDistance() + delta, 1));
        this.setHeight((float) Math.max(this.getHeight() + delta, 0));
    }

    public void handleMouseMovement(double deltaX, double deltaY) {
        double idleThreshold = 2.7;

        if (Math.abs(deltaX) + Math.abs(deltaY) > idleThreshold) {
            this.ticksSinceLastInteraction = 0;
        }
    }
}
