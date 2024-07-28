package io.fabricatedatelier.mayor.block.entity;

import io.fabricatedatelier.mayor.util.boilerplate.AbstractVillageContainerBlockEntity;
import io.fabricatedatelier.mayor.init.BlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class StoneStorageBlockEntity extends AbstractVillageContainerBlockEntity {
    public StoneStorageBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.STONE_STORAGE, pos, state);
    }
}
