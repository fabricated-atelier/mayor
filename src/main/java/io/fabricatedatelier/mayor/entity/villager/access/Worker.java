package io.fabricatedatelier.mayor.entity.villager.access;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface Worker {

    VillagerEntity getVillagerEntity();

    WorkerInventory getWorkerInventory();

    @Nullable
    BlockPos getVillageCenterPosition();

    void setVillageCenterPosition(@Nullable BlockPos villageCenterPosition);

    boolean hasTargetPosition();

    BlockPos getTargetPosition();

    void setTargetPosition(@Nullable BlockPos targetPosition);

    ItemStack getCarryItemStack();

    void setCarryItemStack(ItemStack itemStack);

    /**
     * 0: Nothing
     * 1: Front Carry Task
     * 2: Breaking Task
     * 3: Lumberjack Task
     */
    int getTaskValue();

    /**
     * 0: Nothing
     * 1: Front Carry Task
     * 2: Breaking Task
     * 3: Lumberjack Task
     */
    void setTaskValue(int taskValue);

    default void readWorkerInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (nbt.contains("WorkerInventory", NbtElement.LIST_TYPE)) {
            this.getWorkerInventory().readNbtList(nbt.getList("WorkerInventory", NbtElement.COMPOUND_TYPE), wrapperLookup);
        }
    }

    default void writeWorkerInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbt.put("WorkerInventory", this.getWorkerInventory().toNbtList(wrapperLookup));
    }
}

