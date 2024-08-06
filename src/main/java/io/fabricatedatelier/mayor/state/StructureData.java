package io.fabricatedatelier.mayor.state;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class StructureData {

    private final BlockPos bottomCenterPos;
    private final BlockBox blockBox;
    private final Identifier identifier;
    private int level;

    public StructureData(BlockPos bottomCenterPos, BlockBox blockBox, Identifier identifier, int level) {
        this.bottomCenterPos = bottomCenterPos;
        this.blockBox = blockBox;
        this.identifier = identifier;
        this.level = level;
    }

    public StructureData(NbtCompound nbt) {
        this.bottomCenterPos = NbtHelper.toBlockPos(nbt, "Origin").get();
        this.blockBox = new BlockBox(nbt.getInt("MinX"), nbt.getInt("MinY"), nbt.getInt("MinZ"), nbt.getInt("MaxX"), nbt.getInt("MaxY"), nbt.getInt("MaxZ"));
        this.identifier = Identifier.of(nbt.getString("Identifier"));
        this.level = nbt.getInt("Level");
    }

    public void writeDataToNbt(NbtCompound nbt) {
        nbt.put("Origin", NbtHelper.fromBlockPos(this.bottomCenterPos));
        nbt.putInt("MinX", this.blockBox.getMinX());
        nbt.putInt("MinY", this.blockBox.getMinY());
        nbt.putInt("MinZ", this.blockBox.getMinZ());
        nbt.putInt("MaxX", this.blockBox.getMaxX());
        nbt.putInt("MaxY", this.blockBox.getMaxY());
        nbt.putInt("MaxZ", this.blockBox.getMaxZ());
        nbt.putString("Identifier", this.identifier.toString());
        nbt.putInt("Level", this.level);
    }

    public BlockPos getBottomCenterPos() {
        return bottomCenterPos;
    }

    public BlockBox getBlockBox() {
        return blockBox;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
