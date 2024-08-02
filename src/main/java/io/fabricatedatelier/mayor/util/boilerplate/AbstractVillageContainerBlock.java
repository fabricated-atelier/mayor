package io.fabricatedatelier.mayor.util.boilerplate;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class AbstractVillageContainerBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    protected AbstractVillageContainerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    public static void setOrigin(WorldAccess world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof AbstractVillageContainerBlockEntity blockEntity)) return;
        blockEntity.setStructureOriginPos(pos);
    }

    public static Optional<BlockPos> getOrigin(BlockView world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof AbstractVillageContainerBlockEntity blockEntity))
            return Optional.empty();
        return Optional.of(blockEntity.getStructureOriginPos());
    }

    public static boolean isOrigin(WorldAccess world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof AbstractVillageContainerBlockEntity blockEntity)) return false;
        return blockEntity.isStructureOrigin();
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    abstract public MapCodec<? extends BlockWithEntity> getCodec();

    @Nullable
    @Override
    abstract public BlockEntity createBlockEntity(BlockPos pos, BlockState state);
}
