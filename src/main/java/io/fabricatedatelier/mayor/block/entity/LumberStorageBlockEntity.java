package io.fabricatedatelier.mayor.block.entity;

import io.fabricatedatelier.mayor.init.BlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.function.Consumer;

public class LumberStorageBlockEntity extends AbstractVillageContainerBlockEntity {
    private final SimpleInventory inventory = new SimpleInventory();

    public LumberStorageBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.LUMBER_STORAGE, pos, state);
    }

    @Override
    public StructureDimensions getMaxStructureDimensions() {
        return new StructureDimensions(2, 2, 5);
    }

    public SimpleInventory getInventory() {
        return inventory;
    }

    public void modifyInventorySynced(Consumer<Inventory> consumer) {
        consumer.accept(inventory);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(this.pos);
        }
    }
}
