package io.fabricatedatelier.mayor.block.entity;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;

public abstract class AbstractVillageContainerBlockEntity extends BlockEntity implements HandledInventory {
    private BlockPos structureOriginPos;
    private HashSet<BlockPos> connectedBlocks = new HashSet<>();

    public AbstractVillageContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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

        //TODO: connectedBlockList
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.getItems(), registryLookup);
        this.getStructureOriginPos().ifPresent(originPos -> nbt.putLong(NbtKeys.BLOCK_ENTITY_ORIGIN_POS, originPos.asLong()));

        //TODO: connectedBlockList
    }

    public boolean isStructureOrigin() {
        return this.pos.equals(this.structureOriginPos);
    }

    public Optional<BlockPos> getStructureOriginPos() {
        return Optional.ofNullable(this.structureOriginPos);
    }

    public void setStructureOriginPos(BlockPos structureOriginPos) {
        this.structureOriginPos = structureOriginPos;
    }

    public HashSet<BlockPos> getConnectedBlocks() {
        return this.connectedBlocks;
    }

    public void setConnectedBlocks(HashSet<BlockPos> connectedBlocks) {
        this.connectedBlocks = connectedBlocks;
    }

    public void addConnectedBlocks(BlockPos... pos) {
        for (BlockPos entry : pos) {
            if (entry.equals(this.pos)) continue;
            this.connectedBlocks.add(entry);
        }
    }


// Util

    public abstract StructureDimensions getMaxStructureDimensions();

    public static Optional<HashSet<BlockPos>> getConnectedBlocksFromOrigin(AbstractVillageContainerBlockEntity blockEntity) {
        World world = blockEntity.getWorld();
        if (world == null) return Optional.empty();
        if (blockEntity.getStructureOriginPos().isEmpty()) return Optional.empty();
        if (!(world.getBlockEntity(blockEntity.getStructureOriginPos().get()) instanceof AbstractVillageContainerBlockEntity originBlockEntity))
            return Optional.empty();
        return Optional.ofNullable(originBlockEntity.getConnectedBlocks());
    }


    public record StructureDimensions(int width, int height, int length) {
    }
}
