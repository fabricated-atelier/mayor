package io.fabricatedatelier.mayor.access;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface MinecraftClientAccess {

    public void setStructureBlockMap(Map<BlockPos, BlockState> blockMap);

    @Nullable
    public Map<BlockPos, BlockState> getStructureBlockMap();

    public void setOriginBlockPos(@Nullable BlockPos origin);

    @Nullable
    public BlockPos getOriginBlockPos();
}
