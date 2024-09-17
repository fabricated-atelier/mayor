package io.fabricatedatelier.mayor.entity.custom;

import io.fabricatedatelier.mayor.init.MayorEntities;
import io.fabricatedatelier.mayor.util.NbtKeys;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class CameraTargetEntity extends Entity {
    private static final TrackedData<Optional<UUID>> USER = DataTracker.registerData(CameraTargetEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    public CameraTargetEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public CameraTargetEntity(World world, @Nullable ServerPlayerEntity player) {
        this(MayorEntities.CAMERA_TARGET, world);
        if (player == null) {
            setUser(null);
        } else {
            setUser(player.getUuid());
        }
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
}
