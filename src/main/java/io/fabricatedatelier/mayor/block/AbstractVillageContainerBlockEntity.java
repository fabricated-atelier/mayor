package io.fabricatedatelier.mayor.block;

import io.fabricatedatelier.mayor.api.StorageCallback;
import io.fabricatedatelier.mayor.util.HandledInventory;
import io.fabricatedatelier.mayor.util.NbtKeys;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public abstract class AbstractVillageContainerBlockEntity extends BlockEntity implements HandledInventory {
    private BlockPos structureOriginPos;
    private final HashSet<BlockPos> connectedBlocks = new HashSet<>();
    private StorageCallback callback = null;

    public AbstractVillageContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void registerCallback(StorageCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (!this.isStructureOrigin()) return false;
        return HandledInventory.super.canInsert(slot, stack, dir);
    }

    /**
     * Use this method if you are not concerned about a specific slot.
     * It will test for the condition on the first stack in the inventory.
     */
    public boolean canInsert(ItemStack stack, @Nullable Direction direction) {
        return canInsert(0, stack, direction);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (!this.isStructureOrigin()) return false;
        return HandledInventory.super.canExtract(slot, stack, dir);
    }

    public boolean isStructureOrigin() {
        return this.pos.equals(this.structureOriginPos);
    }

    public Optional<BlockPos> getStructureOriginPos() {
        return Optional.ofNullable(this.structureOriginPos);
    }

    public void setStructureOriginPos(BlockPos newStructureOriginPos) {
        if (callback != null) {
            callback.onOriginChanged(this, new BlockPos(this.structureOriginPos), new BlockPos(newStructureOriginPos));
        }
        this.structureOriginPos = newStructureOriginPos;
        markDirty();
    }

    public boolean insertIntoOrigin(ItemStack stack, @Nullable Direction direction) {
        if (this.getWorld() == null || this.getWorld().isClient()) return false;
        if (this.getStructureOriginPos().isEmpty()) return false;
        if (!(this.getWorld().getBlockEntity(this.getStructureOriginPos().get()) instanceof AbstractVillageContainerBlockEntity blockEntity))
            return false;
        boolean inserted = blockEntity.insert(stack, direction);
        if (inserted) blockEntity.markDirty();
        return inserted;
    }

    public Optional<ItemStack> extractFromOrigin(@Nullable Direction direction) {
        if (this.getWorld() == null || this.getWorld().isClient()) return Optional.empty();
        if (this.getStructureOriginPos().isEmpty()) return Optional.empty();
        if (!(this.getWorld().getBlockEntity(this.getStructureOriginPos().get()) instanceof AbstractVillageContainerBlockEntity blockEntity))
            return Optional.empty();
        Optional<ItemStack> extractedStack = blockEntity.extract(direction);
        if (extractedStack.isPresent()) blockEntity.markDirty();
        return extractedStack;
    }

    public HashSet<BlockPos> getConnectedBlocks() {
        return this.connectedBlocks;
    }

    public void addConnectedBlocks(List<BlockPos> pos) {
        for (BlockPos entry : pos) {
            if (entry.equals(this.pos)) continue;
            this.connectedBlocks.add(entry);
        }
        if (callback != null) {
            callback.onConnectedBlocksChanged(this);
        }
        markDirty();
    }

    public void clearConnectedBlocks() {
        this.getConnectedBlocks().clear();
    }

    public void broadcastNewOriginToConnectedBlocks(WorldAccess world, BlockPos newOriginPos) {
        for (BlockPos connectedPos : getConnectedBlocks()) {
            if (!(world.getBlockEntity(connectedPos) instanceof AbstractVillageContainerBlockEntity blockEntity))
                continue;
            blockEntity.setStructureOriginPos(newOriginPos);
        }
    }

// --- Network & Data ---

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(this.getPos());
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.getItems(), registryLookup);
        if (nbt.contains(NbtKeys.BLOCK_ENTITY_ORIGIN_POS)) {
            this.setStructureOriginPos(BlockPos.fromLong(nbt.getLong(NbtKeys.BLOCK_ENTITY_ORIGIN_POS)));
        }

        this.connectedBlocks.clear();
        NbtCompound blockPosListNbt = nbt.getCompound(NbtKeys.CONNECTED_BLOCKS);
        for (String index : blockPosListNbt.getKeys()) {
            BlockPos connectedPos = BlockPos.fromLong(blockPosListNbt.getLong(index));
            this.connectedBlocks.add(connectedPos);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.getItems(), registryLookup);
        this.getStructureOriginPos().ifPresent(originPos -> nbt.putLong(NbtKeys.BLOCK_ENTITY_ORIGIN_POS, originPos.asLong()));

        List<BlockPos> blockPosList = connectedBlocks.stream().toList();
        NbtCompound blockPosListNbt = new NbtCompound();
        for (int i = 0; i < blockPosList.size(); i++) {
            BlockPos connectedPos = blockPosList.get(i);
            blockPosListNbt.putLong(String.valueOf(i), connectedPos.asLong());
        }
        nbt.put(NbtKeys.CONNECTED_BLOCKS, blockPosListNbt);
    }
}
