package io.fabricatedatelier.mayor.block.custom;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.block.entity.StoneStorageBlockEntity;
import io.fabricatedatelier.mayor.util.boilerplate.AbstractVillageContainerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class StoneStorageBlock extends AbstractVillageContainerBlock {

    public StoneStorageBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.isSolidBlock(world, pos.down())) {
            world.breakBlock(pos, true);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(StoneStorageBlock::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StoneStorageBlockEntity(pos, state);
    }
}
