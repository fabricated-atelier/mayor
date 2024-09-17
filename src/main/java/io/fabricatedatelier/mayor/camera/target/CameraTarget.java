package io.fabricatedatelier.mayor.camera.target;

import net.minecraft.util.math.Vec3d;

/**
 * Get positions of MayorBlockEntities and MayorEntities
 */
public interface CameraTarget {
    Vec3d mayor$getTargetPosition();
}
