package io.fabricatedatelier.mayor.state;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
* To be built structures
*/
public class ConstructionData {

    private final BlockPos bottomCenterPos;
    private final StructureData structureData;
    private final Map<BlockPos, BlockState> blockMap;

    public ConstructionData(BlockPos bottomCenterPos, StructureData structureData, Map<BlockPos, BlockState> blockMap) {
        this.bottomCenterPos = bottomCenterPos;
        this.structureData = structureData;
        this.blockMap = blockMap;
    }

    public ConstructionData(NbtCompound nbt) {
        this.bottomCenterPos = NbtHelper.toBlockPos(nbt, "Origin").get();
        this.structureData = new StructureData(nbt);

        this.blockMap = new HashMap<>();
        for (int i = 0; i < nbt.getInt("Origins"); i++) {
            this.blockMap.put(NbtHelper.toBlockPos(nbt, "Origin" + i).get(), NbtHelper.toBlockState(null, nbt.getCompound("State" + i)));
        }
    }

    public void writeDataToNbt(NbtCompound nbt) {
        nbt.put("Origin", NbtHelper.fromBlockPos(this.bottomCenterPos));
        this.structureData.writeDataToNbt(nbt);

        nbt.putInt("Origins", this.blockMap.size());
        int count = 0;
        for (Map.Entry<BlockPos, BlockState> entry : this.blockMap.entrySet()) {
            nbt.put("Origin" + count, NbtHelper.fromBlockPos(entry.getKey()));
            nbt.put("State" + count, NbtHelper.fromBlockState(entry.getValue()));
            count++;
        }
    }

    @Deprecated
    public NbtCompound writeDataToNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("Origin", NbtHelper.fromBlockPos(this.bottomCenterPos));
        this.structureData.writeDataToNbt(nbt);

        nbt.putInt("Origins", this.blockMap.size());
        int count = 0;
        for (Map.Entry<BlockPos, BlockState> entry : this.blockMap.entrySet()) {
            nbt.put("Origin" + count, NbtHelper.fromBlockPos(entry.getKey()));
            nbt.put("State" + count, NbtHelper.fromBlockState(entry.getValue()));
            count++;
        }
        return nbt;
    }

    public BlockPos getBottomCenterPos() {
        return bottomCenterPos;
    }

    public StructureData getStructureData() {
        return structureData;
    }

    public Map<BlockPos, BlockState> getBlockMap() {
        return blockMap;
    }

}

