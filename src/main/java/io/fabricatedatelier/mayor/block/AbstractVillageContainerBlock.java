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
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractVillageContainerBlock extends BlockWithEntity {
    public static final EnumProperty<MayorProperties.Position> POSITION = MayorProperties.POSITION;

    protected AbstractVillageContainerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(POSITION, MayorProperties.Position.SINGLE)
                .with(Properties.WATERLOGGED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POSITION, Properties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return this.getDefaultState().with(Properties.WATERLOGGED, world.getFluidState(pos).isOf(Fluids.WATER));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        ConnectedBlockUtil.BoundingBox box = new ConnectedBlockUtil.BoundingBox(world, pos, false);
        if (!box.hasHoles() && box.isSquare()) {
            state = getConnectedWallsState(world, state, pos);
            world.setBlockState(pos, state);
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        BlockPos originPos = getOrigin(world, pos).orElse(pos);
        var box = new ConnectedBlockUtil.BoundingBox(world, originPos, false);
        if (!box.hasHoles() && box.isSquare()) {
            state = getConnectedWallsState(world, state, pos);
        } else {
            state = state.with(POSITION, MayorProperties.Position.SINGLE);
        }
        return state;
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

    public static BlockState getConnectedWallsState(WorldAccess world, BlockState state, BlockPos pos) {
        HashSet<Direction> connectedBlocks = getValidConnectedDirections(world, pos);
        for (var entry : MayorProperties.Position.values()) {
            if (entry.getConnectedDirections().equals(connectedBlocks)) state = state.with(POSITION, entry);
        }
        return state;
    }

    private static boolean isSameBlock(BlockState stateA, BlockState stateB) {
        // no instanceof check since we want to know if it's actually the same block and not any of the subclasses
        // ... using the class for that still feels wrong, so change if there is a better solution
        return stateA.getBlock().getClass().equals(stateB.getBlock().getClass());
    }

    public static Map<Direction, BlockState> getConnectedBlockStates(WorldAccess world, BlockPos pos) {
        Map<Direction, BlockState> connectedBlockStates = new HashMap<>();
        for (Direction entry : Direction.Type.HORIZONTAL) {
            connectedBlockStates.put(entry, world.getBlockState(pos.offset(entry)));
        }
        return connectedBlockStates;
    }

    public static HashSet<Direction> getValidConnectedDirections(WorldAccess world, BlockPos pos) {
        HashSet<Direction> validConnections = new HashSet<>();
        BlockState originState = world.getBlockState(pos);
        for (var entry : getConnectedBlockStates(world, pos).entrySet()) {
            if (isSameBlock(originState, world.getBlockState(pos.offset(entry.getKey())))) {
                validConnections.add(entry.getKey());
            }
        }
        return validConnections;
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
