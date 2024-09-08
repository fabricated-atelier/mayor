package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlockEntity;

public interface AbstractStorageCallback {
    void onOriginChanged(AbstractVillageContainerBlockEntity blockEntity);

    void onConnectedBlocksChanged(AbstractVillageContainerBlockEntity blockEntity);
}
