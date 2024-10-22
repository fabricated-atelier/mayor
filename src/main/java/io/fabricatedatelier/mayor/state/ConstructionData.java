package io.fabricatedatelier.mayor.state;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * To be built/demolished structures
 */
public class ConstructionData {

    private final BlockPos bottomCenterPos;
    private final StructureData structureData;
    private final Map<BlockPos, BlockState> blockMap;
    @Nullable
    private UUID villagerUuid;
    private final boolean demolish;

    public ConstructionData(BlockPos bottomCenterPos, StructureData structureData, Map<BlockPos, BlockState> blockMap, @Nullable UUID villagerUuid, boolean demolish) {
        this.bottomCenterPos = bottomCenterPos;
        this.structureData = structureData;
        this.blockMap = blockMap;
        this.villagerUuid = villagerUuid;
        this.demolish = demolish;
    }

    public ConstructionData(NbtCompound nbt) {
        this.bottomCenterPos = NbtHelper.toBlockPos(nbt, "Origin").get();
        this.structureData = new StructureData(nbt);

        this.blockMap = new HashMap<>();
        for (int i = 0; i < nbt.getInt("Origins"); i++) {
            this.blockMap.put(NbtHelper.toBlockPos(nbt, "Origins" + i).get(), NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("State" + i)));
        }
        if (nbt.containsUuid("VillagerUuid")) {
            this.villagerUuid = nbt.getUuid("VillagerUuid");
        } else {
            this.villagerUuid = null;
        }
        this.demolish = nbt.getBoolean("Demolish");
    }

    public void writeDataToNbt(NbtCompound nbt) {
        nbt.put("Origin", NbtHelper.fromBlockPos(this.bottomCenterPos));
        this.structureData.writeDataToNbt(nbt);

        nbt.putInt("Origins", this.blockMap.size());
        int count = 0;
        for (Map.Entry<BlockPos, BlockState> entry : this.blockMap.entrySet()) {
            nbt.put("Origins" + count, NbtHelper.fromBlockPos(entry.getKey()));
            nbt.put("State" + count, NbtHelper.fromBlockState(entry.getValue()));
            count++;
        }
        if (this.villagerUuid != null) {
            nbt.putUuid("VillagerUuid", this.villagerUuid);
        }
        nbt.putBoolean("Demolish", this.demolish);
    }

    public NbtCompound writeDataToNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("Origin", NbtHelper.fromBlockPos(this.bottomCenterPos));
        this.structureData.writeDataToNbt(nbt);

        nbt.putInt("Origins", this.blockMap.size());
        int count = 0;
        for (Map.Entry<BlockPos, BlockState> entry : this.blockMap.entrySet()) {
            nbt.put("Origins" + count, NbtHelper.fromBlockPos(entry.getKey()));
            nbt.put("State" + count, NbtHelper.fromBlockState(entry.getValue()));
            count++;
        }
        if (this.villagerUuid != null) {
            nbt.putUuid("VillagerUuid", this.villagerUuid);
        }
        nbt.putBoolean("Demolish", this.demolish);
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

    @Nullable
    public UUID getVillagerUuid() {
        return this.villagerUuid;
    }

    public void setVillagerUuid(@Nullable UUID villagerUuid) {
        this.villagerUuid = villagerUuid;
    }

    public boolean getDemolish() {
        return demolish;
    }
}

