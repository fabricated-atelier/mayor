package io.fabricatedatelier.mayor.camera.target;

import io.fabricatedatelier.mayor.camera.util.CameraTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class StaticCameraTarget implements CameraTarget {
    private BlockPos pos;

    public StaticCameraTarget(@NotNull BlockPos pos) {
        setPos(pos);
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Vec3d mayor$getTargetPosition() {
        return this.pos.toCenterPos();
    }
}