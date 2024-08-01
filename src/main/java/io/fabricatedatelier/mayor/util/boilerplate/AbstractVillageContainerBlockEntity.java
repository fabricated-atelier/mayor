package io.fabricatedatelier.mayor.util.boilerplate;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Optional;

public abstract class AbstractVillageContainerBlockEntity extends BlockEntity {
    private BlockPos structureOriginPos;
    private HashSet<BlockPos> connectedBlocks = new HashSet<>();

    public AbstractVillageContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract StructureDimensions getMaxStructureDimensions();

    public boolean isStructureOrigin() {
        return this.pos.equals(structureOriginPos);
    }

    public BlockPos getStructureOriginPos() {
        return structureOriginPos;
    }

    public void setStructureOriginPos(BlockPos structureOriginPos) {
        this.structureOriginPos = structureOriginPos;
    }

    public HashSet<BlockPos> getConnectedBlocks() {
        return connectedBlocks;
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

    public static Optional<HashSet<BlockPos>> getConnectedBlocksFromOrigin(AbstractVillageContainerBlockEntity blockEntity) {
        World world = blockEntity.getWorld();
        if (world == null) return Optional.empty();
        if (!(world.getBlockEntity(blockEntity.getStructureOriginPos()) instanceof AbstractVillageContainerBlockEntity originBlockEntity)) return Optional.empty();
        return Optional.ofNullable(originBlockEntity.getConnectedBlocks());
    }




    public record StructureDimensions(int width, int height, int length) { }
}
