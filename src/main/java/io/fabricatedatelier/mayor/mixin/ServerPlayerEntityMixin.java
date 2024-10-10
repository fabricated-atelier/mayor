package io.fabricatedatelier.mayor.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import io.fabricatedatelier.mayor.access.ServerPlayerAccess;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.StateHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerAccess {

    @Shadow
    public abstract ServerWorld getServerWorld();

    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    @Nullable
    @Unique
    private BlockPos titleVillageCenter = null;
    @Unique
    private boolean wasInMayorView = false;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

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

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        VillageData villageData = StateHelper.getClosestVillage(this.getServerWorld(), this.getBlockPos());
        if (villageData != null) {
            if (this.titleVillageCenter == null || !this.titleVillageCenter.equals(villageData.getCenterPos())) {
                this.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.name", villageData.getName())));
                this.titleVillageCenter = villageData.getCenterPos();
            }
        } else if (this.titleVillageCenter != null) {
            this.titleVillageCenter = null;
        }
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
