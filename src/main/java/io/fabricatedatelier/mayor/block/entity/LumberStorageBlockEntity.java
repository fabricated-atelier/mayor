package io.fabricatedatelier.mayor.block.entity;

import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlockEntity;
import io.fabricatedatelier.mayor.datagen.TagProvider;
import io.fabricatedatelier.mayor.init.BlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class LumberStorageBlockEntity extends AbstractVillageContainerBlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(16, ItemStack.EMPTY);

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return stack.isIn(TagProvider.ItemTags.LUMBER_STORAGE_STORABLE);
    }

    public LumberStorageBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.LUMBER_STORAGE, pos, state);
    }
}
