package io.fabricatedatelier.mayor.block.entity;

import io.fabricatedatelier.mayor.init.BlockEntities;
import io.fabricatedatelier.mayor.util.boilerplate.AbstractVillageContainerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class LumberStorageBlockEntity extends AbstractVillageContainerBlockEntity {
    public LumberStorageBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.LUMBER_STORAGE, pos, state);
    }

    @Override
    public StructureDimensions getMaxStructureDimensions() {
        return new StructureDimensions(2, 2, 5);
    }
}
