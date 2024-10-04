package io.fabricatedatelier.mayor.camera;

import io.fabricatedatelier.mayor.camera.util.CameraMode;
import io.fabricatedatelier.mayor.camera.util.CameraTarget;
import io.fabricatedatelier.mayor.camera.target.StaticCameraTarget;
import io.fabricatedatelier.mayor.camera.transition.FadeTransition;
import io.fabricatedatelier.mayor.camera.util.CameraRotation;
import io.fabricatedatelier.mayor.util.TransitionState;
import net.minecraft.client.MinecraftClient;
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

    private static CameraHandler instance;

    @Nullable
    private CameraTarget target;
    @Nullable
    private CameraMode mode;
    private Vec3d cameraPos = Vec3d.ZERO;
    private double pitch, yaw;

    private int tick = 0;
    private float tickDelta = 0;

    private final FadeTransition startFadeTransition, endFadeTransition;

    private CameraHandler(FadeTransition startFadeTransition, FadeTransition endFadeTransition, @Nullable CameraMode mode) {
        this.startFadeTransition = startFadeTransition;
        this.endFadeTransition = endFadeTransition;
        this.mode = mode;

        this.startFadeTransition.startTicking();
    }

    public static CameraHandler getInstance() {
        if (instance == null) instance = new CameraHandler(
                new FadeTransition(MinecraftClient.getInstance(), 200, TransitionState.STARTING),
                new FadeTransition(MinecraftClient.getInstance(), 100, TransitionState.ENDING),
                null
        );
        return instance;
    }

    public Optional<CameraTarget> getTarget() {
        return Optional.ofNullable(target);
    }

    public CameraHandler setTarget(@Nullable CameraTarget target) {
        if (target == null) {
            this.getEndTransition().startTicking();
            return this;
        }
        this.target = target;
        return this;
    }

    public Optional<CameraMode> getMode() {
        return Optional.ofNullable(mode);
    }

    public CameraHandler setMode(@Nullable CameraMode mode) {
        if (mode == null) {
            this.getEndTransition().startTicking();
            return this;
        }
        this.mode = mode;
        return this;
    }

    public void end() {
        setTarget(null);
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

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public void setTickDelta(float tickDelta) {
        this.tickDelta = tickDelta;
    }

    public void tick() {
        if (isFinished()) {
            instance = null;
            return;
        }

        if (getStartTransition().isRunning()) {
            getStartTransition().tick();
        }
        if (getEndTransition().isRunning()) {
            getEndTransition().tick();
        }
        if (getEndTransition().isFinished()) {
            CameraHandler.instance = null;
        }

        if (getTarget().isEmpty()) return;
        if (getMode().isEmpty()) return;

        this.getMode().get().tick(this.getTick(), this::setTick, this.tickDelta);
        updateCameraPos();
        updateCameraRotations();
    }

    public void updateCameraPos() {
        if (this.getTarget().isEmpty() || this.getMode().isEmpty()) return;
        this.setCameraPos(this.getMode().get().updateCameraPos(getTarget().get(), this.tickDelta));
    }

    public void updateCameraRotations() {
        if (this.getTarget().isEmpty() || this.getTarget().get().mayor$getTargetPosition() == null) return;
        this.getMode().ifPresent(cameraMode -> {
            CameraRotation rotation = cameraMode.updateCameraRotations(this.getTarget().get(), this.getCameraPos(), this.tickDelta);
            if (rotation == null) return;
            this.setPitch(Math.toDegrees(rotation.pitchInRad()));
            this.setYaw(Math.toDegrees(rotation.yawInRad()) - 90);
        });
    }

    public void handleScroll(double delta) {
        this.getMode().ifPresent(cameraMode -> cameraMode.handleScroll(delta));
    }

    public void handleMouseMovement(double deltaX, double deltaY) {
        this.getMode().ifPresent(cameraMode -> cameraMode.handleMouseMovement(deltaX, deltaY));
    }
}
