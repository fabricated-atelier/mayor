package io.fabricatedatelier.mayor.block;

import io.fabricatedatelier.mayor.util.HandledInventory;
import io.fabricatedatelier.mayor.util.NbtKeys;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public abstract class AbstractVillageContainerBlockEntity extends BlockEntity implements HandledInventory {
    private BlockPos structureOriginPos;
    private final HashSet<BlockPos> connectedBlocks = new HashSet<>();

    public AbstractVillageContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

    public boolean isStructureOrigin() {
        return this.pos.equals(this.structureOriginPos);
    }

    public Optional<BlockPos> getStructureOriginPos() {
        return Optional.ofNullable(this.structureOriginPos);
    }

    public void setStructureOriginPos(BlockPos structureOriginPos) {
        this.structureOriginPos = structureOriginPos;
        markDirty();
    }

    public HashSet<BlockPos> getConnectedBlocks() {
        return this.connectedBlocks;
    }

    public void addConnectedBlocks(BlockPos... pos) {
        for (BlockPos entry : pos) {
            if (entry.equals(this.pos)) continue;
            this.connectedBlocks.add(entry);
        }
        markDirty();
    }

// Network

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


// Util

    public StructureDimensions getMaxStructureDimensions() {
        return new StructureDimensions(3);
    }

    public static Optional<HashSet<BlockPos>> getConnectedBlocksFromOrigin(AbstractVillageContainerBlockEntity blockEntity) {
        World world = blockEntity.getWorld();
        if (world == null) return Optional.empty();
        if (blockEntity.getStructureOriginPos().isEmpty()) return Optional.empty();
        if (!(world.getBlockEntity(blockEntity.getStructureOriginPos().get()) instanceof AbstractVillageContainerBlockEntity originBlockEntity))
            return Optional.empty();
        return Optional.ofNullable(originBlockEntity.getConnectedBlocks());
    }


    public record StructureDimensions(int width, int height, int length) {
        public StructureDimensions(int length) {
            this(length, length, length);
        }
    }
}
