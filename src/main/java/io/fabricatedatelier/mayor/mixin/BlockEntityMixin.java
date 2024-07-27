package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.camera.target.CameraTarget;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements CameraTarget {

    @Override
    public Vec3d mayor$getTargetPosition() {
        return ((BlockEntity) (Object) this).getPos().toCenterPos().add(0, 0.5, 0);
    }
}
