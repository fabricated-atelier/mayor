package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlockEntity;
import net.minecraft.util.math.BlockPos;

public interface StorageCallback {
    void onOriginChanged(AbstractVillageContainerBlockEntity blockEntity, BlockPos oldPos, BlockPos newPos);

    void onConnectedBlocksChanged(AbstractVillageContainerBlockEntity blockEntity);
}
