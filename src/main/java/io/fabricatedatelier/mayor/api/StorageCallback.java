package io.fabricatedatelier.mayor.api;

import io.fabricatedatelier.mayor.block.entity.VillageContainerBlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * If you want to listen to the {@link VillageContainerBlockEntity StorageBlockEntity}
 * {@link StorageCallback callbacks}, you will need to register the callback first.<br>
 * In the class, where you want to listen to those signals, implement the {@link StorageCallback} interface.<br>
 * Then wherever you get access to the current {@link VillageContainerBlockEntity StorageBlockEntity},
 * call its {@link VillageContainerBlockEntity#registerCallback(StorageCallback) registerCallback()} method
 * with your current class's instance as the parameter (usually just a <code>this</code> call)
 */
public interface StorageCallback {
    /**
     * Runs when the origin BlockPos of an {@link VillageContainerBlockEntity} has been changed.
     * @param blockEntity the BlockEntity which changed its listed origin BlockPos
     * @param oldPos the old listed entry of the origin BlockPos
     * @param newPos the new listed entry of the origin BlockPos
     */
    default void onOriginChanged(VillageContainerBlockEntity blockEntity, BlockPos oldPos, BlockPos newPos) {

    }

    /**
     * Runs when the connected blocks BlockPos list of an {@link VillageContainerBlockEntity} has been changed.
     * @param blockEntity the BlockEntity which changed its connected blocks BlockPos list
     */
    default void onConnectedBlocksChanged(VillageContainerBlockEntity blockEntity) {

    }
}
