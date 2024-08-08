package io.fabricatedatelier.mayor.manager;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.List;
import java.util.Map;

public class MayorStructure {

    private final Identifier identifier;
    private final int level;
    private final MayorCategory.BiomeCategory biomeCategory;
    private final MayorCategory.BuildingCategory buildingCategory;
    private final List<ItemStack> requiredItemStacks;
    private final Map<BlockPos, BlockState> blockMap;
    private final Vec3i size;

    public MayorStructure(Identifier identifier, int level, MayorCategory.BiomeCategory biomeCategory, MayorCategory.BuildingCategory buildingCategory, List<ItemStack> requiredItemStacks, Map<BlockPos, BlockState> blockMap, Vec3i size) {
        this.identifier = identifier;
        this.level = level;
        this.biomeCategory = biomeCategory;
        this.buildingCategory = buildingCategory;
        this.requiredItemStacks = requiredItemStacks;
        this.blockMap = blockMap;
        this.size = size;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public int getLevel() {
        return level;
    }

    public MayorCategory.BiomeCategory getBiomeCategory() {
        return biomeCategory;
    }

    public MayorCategory.BuildingCategory getBuildingCategory() {
        return buildingCategory;
    }

    public List<ItemStack> getRequiredItemStacks() {
        return requiredItemStacks;
    }

    public Map<BlockPos, BlockState> getBlockMap() {
        return blockMap;
    }

    public Vec3i getSize() {
        return size;
    }
}
