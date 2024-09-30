package io.fabricatedatelier.mayor.entity.custom;

import io.fabricatedatelier.mayor.camera.util.CameraTarget;
import io.fabricatedatelier.mayor.init.MayorEntities;
import io.fabricatedatelier.mayor.util.NbtKeys;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class CameraPullEntity extends Entity implements CameraTarget {
    private static final TrackedData<Optional<UUID>> USER = DataTracker.registerData(CameraPullEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    public CameraPullEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public CameraPullEntity(World world, @Nullable ServerPlayerEntity player) {
        this(MayorEntities.CAMERA_PULL, world);
        if (player == null) setUser(null);
        else setUser(player.getUuid());
    }

    public Optional<UUID> getUser() {
        return this.dataTracker.get(USER);
    }

    public void setUser(@Nullable UUID user) {
        this.dataTracker.set(USER, Optional.ofNullable(user));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(USER, Optional.empty());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains(NbtKeys.USER_UUID)) {
            setUser(nbt.getUuid(NbtKeys.USER_UUID));
        } else {
            setUser(null);
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        getUser().ifPresentOrElse(userUUID -> {
            nbt.putUuid(NbtKeys.USER_UUID, userUUID);
        }, () -> nbt.remove(NbtKeys.USER_UUID));
    }

    public Optional<PlayerEntity> getUserPlayer() {
        return getUser().flatMap(uuid -> Optional.ofNullable(this.getWorld().getPlayerByUuid(uuid)));
    }

    @Override
    public Vec3d mayor$getTargetPosition() {
        return this.getPos();
    }
}
