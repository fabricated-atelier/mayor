package io.fabricatedatelier.mayor.block;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.block.entity.AbstractVillageContainerBlockEntity;
import io.fabricatedatelier.mayor.util.ConnectedBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class AbstractVillageContainerBlock extends BlockWithEntity {
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;

    protected AbstractVillageContainerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(Properties.WATERLOGGED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, Properties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return this.getDefaultState().with(Properties.WATERLOGGED, world.getFluidState(pos).isOf(Fluids.WATER));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        state = getConnectedWallsState(world, state, pos);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        state = getConnectedWallsState(world, state, pos);
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.FLOWING_WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    @Override
    abstract public MapCodec<? extends BlockWithEntity> getCodec();

    @Nullable
    @Override
    abstract public BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    public static boolean isSingle(BlockState state) {
        if (state.contains(NORTH) && state.get(NORTH)) return false;
        if (state.contains(EAST) && state.get(EAST)) return false;
        if (state.contains(SOUTH) && state.get(SOUTH)) return false;
        return !state.contains(WEST) || !state.get(WEST);
    }

    public static BlockState getConnectedWallsState(WorldAccess world, BlockState state, BlockPos pos) {
        ConnectedBlockUtil.BoundingBox box = new ConnectedBlockUtil.BoundingBox(world, pos, false);
        if (box.getConnectedPosList().size() < 2) return state;
        if (!box.hasHoles() && box.isSquare()) {
            if (world.getBlockState(pos.offset(Direction.NORTH)).getBlock().getClass().equals(state.getBlock().getClass())) {
                state = state.with(NORTH, true);
            }
            if (world.getBlockState(pos.offset(Direction.EAST)).getBlock().getClass().equals(state.getBlock().getClass())) {
                state = state.with(EAST, true);
            }
            if (world.getBlockState(pos.offset(Direction.SOUTH)).getBlock().getClass().equals(state.getBlock().getClass())) {
                state = state.with(SOUTH, true);
            }
            if (world.getBlockState(pos.offset(Direction.WEST)).getBlock().getClass().equals(state.getBlock().getClass())) {
                state = state.with(WEST, true);
            }
        }
        return state;
    }

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
