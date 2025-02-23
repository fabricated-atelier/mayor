package io.fabricatedatelier.mayor.block.entity;

import io.fabricatedatelier.mayor.api.StorageCallback;
import io.fabricatedatelier.mayor.init.MayorBlockEntities;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.HandledInventory;
import io.fabricatedatelier.mayor.util.StateHelper;
import io.fabricatedatelier.mayor.util.NbtKeys;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VillageContainerBlockEntity extends BlockEntity implements HandledInventory {
    public static final Map<Integer, Integer> MAX_STACK_COUNT_FROM_STRUCTURE_SIZE = Map.of(
            1, 3,
            2, 12,
            3, 27
    );

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(16, ItemStack.EMPTY);
    private BlockPos structureOriginPos;
    private final List<BlockPos> connectedBlocks = new ArrayList<>();

    private TagKey<Item> insertableItems;
    private StorageCallback callback = null;

    public VillageContainerBlockEntity(BlockPos pos, BlockState state) {
        super(MayorBlockEntities.VILLAGE_STORAGE, pos, state);
    }

    public VillageContainerBlockEntity(BlockPos pos, BlockState state, TagKey<Item> insertableItems) {
        this(pos, state);
        this.insertableItems = insertableItems;
    }

    public void registerCallback(StorageCallback callback) {
        this.callback = callback;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (!this.isStructureOrigin() || this.getWorld() == null) return false;
return true;
//        if (!stack.isIn(this.insertableItems)) return false;
//        //TODO: only allow if valid size
//        return HandledInventory.super.canInsert(slot, stack, dir);
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

    public boolean isFull(@Nullable ItemStack stack) {
        if (!this.isStructureOrigin()) {
            return true;
        }
        if (stack != null) {
            ItemStack copied = stack.copy();
            for (ItemStack itemStack : this.getItems()) {
                if (itemStack.isEmpty()) {
                    return false;
                } else if (itemStack.isOf(copied.getItem()) && itemStack.getCount() < itemStack.getMaxCount()) {
                    copied.decrement(itemStack.getMaxCount() - itemStack.getCount());
                    if (copied.getCount() <= 0) {
                        return false;
                    }
                }
            }
        } else {
            for (ItemStack itemStack : this.getItems()) {
                if (itemStack.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isStructureOrigin() {
        return this.pos.equals(this.structureOriginPos);
    }

    public Optional<BlockPos> getStructureOriginPos() {
        return Optional.ofNullable(this.structureOriginPos);
    }

    public void setStructureOriginPos(BlockPos newStructureOriginPos) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            VillageData villageData = StateHelper.getClosestVillage(serverWorld, this.getPos());
            if (villageData != null) {
                if (this.structureOriginPos == null) {
                    villageData.addStorageOriginBlockPos(newStructureOriginPos);
                    StateHelper.getMayorVillageState(serverWorld).markDirty();
                } else if (!this.structureOriginPos.equals(newStructureOriginPos)) {
                    villageData.removeStorageOriginBlockPos(this.structureOriginPos);
                    villageData.addStorageOriginBlockPos(newStructureOriginPos);
                    StateHelper.getMayorVillageState(serverWorld).markDirty();
                }
            }
        }


        if (callback != null) {
            callback.onOriginChanged(this, new BlockPos(this.structureOriginPos), new BlockPos(newStructureOriginPos));
        }
        this.structureOriginPos = newStructureOriginPos;

        markDirty();
    }

    public Optional<VillageContainerBlockEntity> getStructureOriginBlockEntity() {
        if (this.world == null) return Optional.empty();
        if (this.getStructureOriginPos().isEmpty()) return Optional.empty();
        if (!(world.getBlockEntity(this.getStructureOriginPos().get()) instanceof VillageContainerBlockEntity blockEntity))
            return Optional.empty();
        return Optional.of(blockEntity);
    }

    public boolean insertIntoOrigin(ItemStack stack, @Nullable Direction direction) {
        if (this.getWorld() == null || this.getWorld().isClient()) return false;
        if (this.getStructureOriginPos().isEmpty()) return false;
        if (!(this.getWorld().getBlockEntity(this.getStructureOriginPos().get()) instanceof VillageContainerBlockEntity blockEntity))
            return false;
        boolean inserted = blockEntity.insert(stack, direction);
        if (inserted) blockEntity.markDirty();
        return inserted;
    }

    public Optional<ItemStack> extractFromOrigin(@Nullable Direction direction) {
        if (this.getWorld() == null || this.getWorld().isClient()) return Optional.empty();
        if (this.getStructureOriginPos().isEmpty()) return Optional.empty();
        if (!(this.getWorld().getBlockEntity(this.getStructureOriginPos().get()) instanceof VillageContainerBlockEntity blockEntity))
            return Optional.empty();
        Optional<ItemStack> extractedStack = blockEntity.extract(direction);
        if (extractedStack.isPresent()) blockEntity.markDirty();
        return extractedStack;
    }



    public List<BlockPos> getConnectedBlocks() {
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

    public void moveConnectedBlocks(VillageContainerBlockEntity newOriginBlockEntity) {
        newOriginBlockEntity.getConnectedBlocks().clear();
        newOriginBlockEntity.addConnectedBlocks(this.getConnectedBlocks());
    }

    public void broadcastNewOriginPos(WorldAccess world, BlockPos newOriginPos) {
        for (BlockPos connectedPos : getConnectedBlocks()) {
            if (!(world.getBlockEntity(connectedPos) instanceof VillageContainerBlockEntity blockEntity))
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
