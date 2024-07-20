package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.util.CameraTarget;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements CameraTarget {

    @Override
    public Vec3d mayor$getTargetPosition() {
        return ((BlockEntity) (Object) this).getPos().toCenterPos();
    }
}
