package io.fabricatedatelier.mayor.block;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.block.entity.AbstractVillageContainerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected AbstractVillageContainerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(WATERLOGGED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.FLOWING_WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
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


    public static void setOrigin(WorldAccess world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof AbstractVillageContainerBlockEntity blockEntity)) return;
        blockEntity.setStructureOriginPos(pos);
    }

    public static Optional<BlockPos> getOrigin(BlockView world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof AbstractVillageContainerBlockEntity blockEntity))
            return Optional.empty();
        return blockEntity.getStructureOriginPos();
    }

    public static boolean isOrigin(WorldAccess world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof AbstractVillageContainerBlockEntity blockEntity)) return false;
        return blockEntity.isStructureOrigin();
    }
}
