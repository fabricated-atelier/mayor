package io.fabricatedatelier.mayor.entity.villager.access;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface Builder {

    VillagerEntity getVillagerEntity();

    BuilderInventory getBuilderInventory();

    @Nullable
    BlockPos getVillageCenterPosition();

    void setVillageCenterPosition(@Nullable BlockPos villageCenterPosition);

    boolean hasTargetPosition();

    BlockPos getTargetPosition();

    void setTargetPosition(@Nullable BlockPos targetPosition);

    ItemStack getCarryItemStack();

    void setCarryItemStack(ItemStack itemStack);

    default void readBuilderInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (nbt.contains("BuilderInventory", NbtElement.LIST_TYPE)) {
            this.getBuilderInventory().readNbtList(nbt.getList("BuilderInventory", NbtElement.COMPOUND_TYPE), wrapperLookup);
        }
    }

    default void writeBuilderInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbt.put("BuilderInventory", this.getBuilderInventory().toNbtList(wrapperLookup));
    }
}

