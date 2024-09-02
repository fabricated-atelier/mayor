package io.fabricatedatelier.mayor.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface HandledInventory extends SidedInventory {
    DefaultedList<ItemStack> getItems();

    default int size() {
        return getItems().size();
    }

    @Override
    default int[] getAvailableSlots(Direction side) {
        List<Integer> availableSlots = new ArrayList<>();
        for (int i = 0; i < getItems().size(); i++) {
            availableSlots.add(i);
        }
        return availableSlots.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    default boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !isEmpty();
    }

    @Override
    default boolean isEmpty() {
        return size() <= 0;
    }

    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        markDirty();
    }

    default boolean tryAddingStack(ItemStack stack) {
        boolean stackWasAdded = false;
        for (int i = 0; i < size(); i++) {
            if (!getItems().get(i).equals(ItemStack.EMPTY)) continue;
            setStack(i, stack);
            stackWasAdded = true;
            break;
        }
        markDirty();
        return stackWasAdded;
    }

    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    @Override
    default ItemStack removeStack(int slot, int amount) {
        ItemStack stack = Inventories.splitStack(getItems(), slot, amount);
        if (!stack.isEmpty()) markDirty();
        return stack;
    }

    default boolean contains(Item item) {
        for (ItemStack stack : this.getItems()) {
            if (stack.getItem().equals(item)) return true;
        }
        return false;
    }

    default boolean contains(ItemStack stack) {
        for (ItemStack entry : this.getItems()) {
            if (entry.equals(stack)) return true;
        }
        return false;
    }

    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    default void clear() {
        getItems().clear();
        markDirty();
    }

    default void markDirty() {

    }
}
