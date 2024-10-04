package io.fabricatedatelier.mayor.state;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

/**
 * Built structures
 */
public class StructureData {

    private final BlockPos bottomCenterPos;
    private final BlockBox blockBox;
    private final int rotation;
    private final Identifier identifier;
    private int level;
    private int experience;

    public StructureData(BlockPos bottomCenterPos, BlockBox blockBox, int rotation, Identifier identifier, int level, int experience) {
        this.bottomCenterPos = bottomCenterPos;
        this.blockBox = blockBox;
        this.rotation = rotation;
        this.identifier = identifier;
        this.level = level;
        this.experience = experience;
    }

    public StructureData(NbtCompound nbt) {
        this.bottomCenterPos = NbtHelper.toBlockPos(nbt, "Origin").get();
        this.blockBox = new BlockBox(nbt.getInt("MinX"), nbt.getInt("MinY"), nbt.getInt("MinZ"), nbt.getInt("MaxX"), nbt.getInt("MaxY"), nbt.getInt("MaxZ"));
        this.rotation = nbt.getInt("Rotation");
        this.identifier = Identifier.of(nbt.getString("Identifier"));
        this.level = nbt.getInt("Level");
        this.experience = nbt.getInt("Experience");
    }

    public void writeDataToNbt(NbtCompound nbt) {
        nbt.put("Origin", NbtHelper.fromBlockPos(this.bottomCenterPos));
        nbt.putInt("MinX", this.blockBox.getMinX());
        nbt.putInt("MinY", this.blockBox.getMinY());
        nbt.putInt("MinZ", this.blockBox.getMinZ());
        nbt.putInt("MaxX", this.blockBox.getMaxX());
        nbt.putInt("MaxY", this.blockBox.getMaxY());
        nbt.putInt("MaxZ", this.blockBox.getMaxZ());
        nbt.putInt("Rotation", this.rotation);
        nbt.putString("Identifier", this.identifier.toString());
        nbt.putInt("Level", this.level);
        nbt.putInt("Experience", this.experience);
    }

    public NbtCompound writeDataToNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("Origin", NbtHelper.fromBlockPos(this.bottomCenterPos));
        nbt.putInt("MinX", this.blockBox.getMinX());
        nbt.putInt("MinY", this.blockBox.getMinY());
        nbt.putInt("MinZ", this.blockBox.getMinZ());
        nbt.putInt("MaxX", this.blockBox.getMaxX());
        nbt.putInt("MaxY", this.blockBox.getMaxY());
        nbt.putInt("MaxZ", this.blockBox.getMaxZ());
        nbt.putInt("Rotation", this.rotation);
        nbt.putString("Identifier", this.identifier.toString());
        nbt.putInt("Level", this.level);
        nbt.putInt("Experience", this.experience);
        return nbt;
    }


    public BlockPos getBottomCenterPos() {
        return bottomCenterPos;
    }

    public BlockBox getBlockBox() {
        return blockBox;
    }

    public int getRotation() {
        return rotation;
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

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }
}
