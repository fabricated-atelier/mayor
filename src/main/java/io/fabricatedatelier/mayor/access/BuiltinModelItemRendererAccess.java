package io.fabricatedatelier.mayor.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public interface BuiltinModelItemRendererAccess {

    public void setWorldAndBlockState(World world, BlockState blockState);
}
