package io.fabricatedatelier.mayor.util;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class MayorManager {

    @Nullable
    private Identifier structureId = null;
    @Nullable
    private Map<BlockPos, BlockState> blockMap = new HashMap<BlockPos, BlockState>();
    @Nullable
    private BlockPos originBlockPos = null;
    private BlockRotation structureRotation = BlockRotation.NONE;
    private boolean center = false;

    private boolean isInMajorView = false;

    private final PlayerEntity playerEntity;

    public MayorManager(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public PlayerEntity playerEntity() {
        return this.playerEntity;
    }

    public void setStructureId(Identifier structureId) {
        this.structureId = structureId;
    }

    @Nullable
    public Identifier getStructureId() {
        return this.structureId;
    }

    public void setStructureBlockMap(Map<BlockPos, BlockState> blockMap) {
        this.blockMap.clear();
        this.blockMap = blockMap;
    }

    @Nullable
    public Map<BlockPos, BlockState> getStructureBlockMap() {
        return this.blockMap;
    }

    public void setOriginBlockPos(@Nullable BlockPos origin) {
        this.originBlockPos = origin;
    }

    @Nullable
    public BlockPos getOriginBlockPos() {
        return this.originBlockPos;
    }

    public void setStructureRotation(BlockRotation structureRotation) {
        this.structureRotation = structureRotation;
    }

    public BlockRotation getStructureRotation() {
        return this.structureRotation;
    }

    public void setStructureCentered(boolean center){
        this.center = center;
    }

    public boolean getStructureCentered(){
        return this.center;
    }

    public void setMajorView(boolean majorView) {
        this.isInMajorView = majorView;
    }

    public boolean isInMajorView() {
        return this.isInMajorView;
    }
}
