package io.fabricatedatelier.mayor.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.fabricatedatelier.mayor.access.ServerPlayerAccess;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ServerPlayerAccess {


    @Unique
    private boolean wasInMayorView = false;

    @Inject(method = "setCameraEntity", at = @At("TAIL"))
    private void setCameraEntityMixin(@Nullable Entity entity, CallbackInfo info) {
        if (entity == null) {
            this.wasInMayorView = false;
        }
    }

    @WrapWithCondition(method = "setCameraEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;teleport(Lnet/minecraft/server/world/ServerWorld;DDDLjava/util/Set;FF)Z"))
    private boolean setCameraEntityMixin(ServerPlayerEntity instance, ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch) {
        return !this.wasInMayorView;
    }

    @Override
    public void setWasInMayorView(boolean setWasInMayorView) {
        this.wasInMayorView = setWasInMayorView;
    }

    @Override
    public boolean wasInMayorView() {
        return this.wasInMayorView;
    }
}
