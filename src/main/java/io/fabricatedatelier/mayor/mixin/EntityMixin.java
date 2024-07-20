package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.util.CameraTarget;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class EntityMixin implements CameraTarget {

    @Shadow public int age;

    @Override
    public Vec3d mayor$getTargetPosition() {
        return ((Entity) (Object) this).getPos();
    }
}
