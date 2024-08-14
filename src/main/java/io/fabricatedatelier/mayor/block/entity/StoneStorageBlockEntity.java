package io.fabricatedatelier.mayor.block.entity;

import io.fabricatedatelier.mayor.init.BlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class StoneStorageBlockEntity extends AbstractVillageContainerBlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(8, ItemStack.EMPTY);

    public StoneStorageBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.STONE_STORAGE, pos, state);
    }

    @Override
    public StructureDimensions getMaxStructureDimensions() {
        return new StructureDimensions(3, 2, 3);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }
}
