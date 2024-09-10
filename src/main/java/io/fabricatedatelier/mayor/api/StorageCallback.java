package io.fabricatedatelier.mayor.api;

import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * If you want to listen to the {@link AbstractVillageContainerBlockEntity StorageBlockEntity}
 * {@link StorageCallback callbacks}, you will need to register the callback first.<br>
 * In the class, where you want to listen to those signals, implement the {@link StorageCallback} interface.<br>
 * Then wherever you get access to the current {@link AbstractVillageContainerBlockEntity StorageBlockEntity},
 * call its {@link AbstractVillageContainerBlockEntity#registerCallback(StorageCallback) registerCallback()} method
 * with your current class's instance as the parameter (usually just a <code>this</code> call)
 */
public interface StorageCallback {
    /**
     * Runs when the origin BlockPos of an {@link AbstractVillageContainerBlockEntity} has been changed.
     * @param blockEntity the BlockEntity which changed its listed origin BlockPos
     * @param oldPos the old listed entry of the origin BlockPos
     * @param newPos the new listed entry of the origin BlockPos
     */
    default void onOriginChanged(AbstractVillageContainerBlockEntity blockEntity, BlockPos oldPos, BlockPos newPos) {

    }

    /**
     * Runs when the connected blocks BlockPos list of an {@link AbstractVillageContainerBlockEntity} has been changed.
     * @param blockEntity the BlockEntity which changed its connected blocks BlockPos list
     */
    default void onConnectedBlocksChanged(AbstractVillageContainerBlockEntity blockEntity) {

    }
}
