package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.camera.CameraHelper;
import io.fabricatedatelier.mayor.camera.target.CameraTarget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements CameraTarget {

    @Override
    public Vec3d mayor$getTargetPosition() {
        return ((Entity) (Object) this).getPos();
    }

    @Inject(method = "interact", at = @At("TAIL"))
    private void setAsCameraTarget(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!(player instanceof ClientPlayerEntity)) return;
        CameraHelper.getInstance().setTarget(this);
    }

    //TODO: maybe at head instead with a cancel to lock rotation for entity?
    @Inject(method = "changeLookDirection", at = @At(value = "TAIL"))
    private void captureMouseMovement(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if (cursorDeltaX == 0 && cursorDeltaY == 0) return;
        if (!((Entity) (Object) this instanceof ClientPlayerEntity)) return;
        CameraHelper camera = CameraHelper.getInstance();
        if (!camera.hasTarget()) return;

        camera.handleMouseMovement(cursorDeltaX, cursorDeltaY);
    }
}
