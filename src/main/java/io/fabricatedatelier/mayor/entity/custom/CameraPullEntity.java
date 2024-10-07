package io.fabricatedatelier.mayor.entity.custom;

import io.fabricatedatelier.mayor.camera.CameraHandler;
import io.fabricatedatelier.mayor.camera.mode.FreeFlyMode;
import io.fabricatedatelier.mayor.camera.util.CameraTarget;
import io.fabricatedatelier.mayor.util.NbtKeys;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class CameraPullEntity extends Entity implements CameraTarget {
    @Nullable
    private DirectionInput movementInput = null;
    private int tick = 0;
    private static final TrackedData<Optional<UUID>> USER = DataTracker.registerData(CameraPullEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    public CameraPullEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public Optional<UUID> getUser() {
        return this.dataTracker.get(USER);
    }

    public void setUser(@Nullable UUID user) {
        this.dataTracker.set(USER, Optional.ofNullable(user));
    }

    public Optional<DirectionInput> getMovementInput() {
        return Optional.ofNullable(movementInput);
    }

    public void setMovementInput(@Nullable DirectionInput movementInput) {
        this.movementInput = movementInput;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(USER, Optional.empty());
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        setUser(player.getUuid());
        if (player.getWorld().isClient()) {
            CameraHandler.getInstance().setMode(new FreeFlyMode(this)).setTarget(this);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();
        tick++;
        if (tick % 20 != 0) return;
        double speed = 1.5;
        Vec3d input = getMovementInput().map(DirectionInput::getDirection).orElse(new Vec3d(0, 0, 0));
        this.addVelocity(input.multiply(speed));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains(NbtKeys.USER_UUID)) setUser(nbt.getUuid(NbtKeys.USER_UUID));
        else setUser(null);

        if (nbt.contains(NbtKeys.DIRECTION_INPUT))
            setMovementInput(DirectionInput.valueOf(nbt.getString(NbtKeys.DIRECTION_INPUT)));
        else setMovementInput(null);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        getUser().ifPresentOrElse(userUUID -> nbt.putUuid(NbtKeys.USER_UUID, userUUID), () -> nbt.remove(NbtKeys.USER_UUID));
        getMovementInput().ifPresentOrElse(directionInput -> nbt.putString(NbtKeys.DIRECTION_INPUT, directionInput.name()), () -> nbt.remove(NbtKeys.DIRECTION_INPUT));
    }

    public static boolean hasCorrectUUID(Entity entity, ServerPlayerEntity player) {
        if (!(entity instanceof CameraPullEntity cameraPullEntity)) return false;
        if (cameraPullEntity.getUser().isEmpty()) return false;
        return cameraPullEntity.getUser().get().equals(player.getUuid());
    }

    @Override
    public Vec3d mayor$getTargetPosition() {
        return this.getPos();
    }

    public enum DirectionInput {
        FORWARD(new Vec3d(0, 0, 1)),
        BACKWARD(new Vec3d(0, 0, -1)),
        LEFT(new Vec3d(1, 0, 0)),
        RIGHT(new Vec3d(-1, 0, 0)),
        UP(new Vec3d(0, 1, 0)),
        DOWN(new Vec3d(0, -1, 0));

        private final Vec3d direction;

        DirectionInput(Vec3d direction) {
            this.direction = direction;
        }

        public Vec3d getDirection() {
            return direction;
        }
    }
}
