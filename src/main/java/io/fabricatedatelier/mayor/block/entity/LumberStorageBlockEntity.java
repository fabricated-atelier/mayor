package io.fabricatedatelier.mayor.block.entity;

import io.fabricatedatelier.mayor.init.BlockEntities;
import io.fabricatedatelier.mayor.util.HandledInventory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class LumberStorageBlockEntity extends AbstractVillageContainerBlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(16, ItemStack.EMPTY);

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public LumberStorageBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.LUMBER_STORAGE, pos, state);
    }

    @Override
    public StructureDimensions getMaxStructureDimensions() {
        return new StructureDimensions(2, 2, 5);
    }

    public void modifyInventorySynced(Consumer<HandledInventory> consumer) {
        consumer.accept(this);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(this.pos);
        }
    }
}
