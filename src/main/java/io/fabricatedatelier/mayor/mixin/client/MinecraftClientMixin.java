package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.access.MinecraftClientAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements MinecraftClientAccess {

    @Unique
    @Nullable
    private Map<BlockPos, BlockState> blockMap = new HashMap<BlockPos, BlockState>();
    @Unique
    @Nullable
    private BlockPos originBlockPos = null;

    @Override
    public @Nullable Map<BlockPos, BlockState> getStructureBlockMap() {
        return this.blockMap;
    }

    @Override
    public void setStructureBlockMap(Map<BlockPos, BlockState> blockMap) {
        this.blockMap.clear();
        this.blockMap = blockMap;
    }

    @Override
    public void setOriginBlockPos(@Nullable BlockPos origin) {
        this.originBlockPos = origin;
    }

    @Override
    public @Nullable BlockPos getOriginBlockPos() {
        return this.originBlockPos;
    }
}
