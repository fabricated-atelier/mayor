package io.fabricatedatelier.mayor.block.custom;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlock;
import io.fabricatedatelier.mayor.block.Properties;
import io.fabricatedatelier.mayor.block.entity.LumberStorageBlockEntity;
import io.fabricatedatelier.mayor.block.voxelshape.LumberBlockVoxelShapes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class LumberStorageBlock extends AbstractVillageContainerBlock {

    public LumberStorageBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(Properties.SHAPE, Properties.Shape.ALL_WALLS)
                .with(Properties.POSITION, Properties.VerticalPosition.BOTTOM)
                .with(Properties.SIDE, Properties.Side.RIGHT));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.SHAPE, Properties.POSITION, Properties.SIDE);
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(LumberStorageBlock::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LumberStorageBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        BlockState state = super.getPlacementState(ctx);
        BlockState targetedState = ctx.getWorld().getBlockState(ctx.getBlockPos());

        if (state == null) return null;

        //TODO: use else branch if it exceeded the structure size
        if (targetedState.getBlock() instanceof LumberStorageBlock) {
            state = state.with(FACING, targetedState.get(FACING));
        } else {
            state = state.with(FACING, ctx.getHorizontalPlayerFacing());
        }

        if (world.getBlockState(pos.down()).getBlock() instanceof LumberStorageBlock) {
            state = state.with(Properties.POSITION, Properties.VerticalPosition.TOP);
        } else {
            state = state.with(Properties.POSITION, Properties.VerticalPosition.BOTTOM);
        }
        state = getShape(world, pos, state);

        if (state.get(Properties.SHAPE).equals(Properties.Shape.ALL_WALLS)) {
            setOrigin(world, pos);
        }

        return state;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        // setBlockState is still possible, use BlockItem for that
        // if (isNextToSameBlock(world, pos) || !isSupported(world, pos)) return false;
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!isNextToSameBlock(world, pos)) {
            setOrigin(world, pos);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        BlockState finalState = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        if (!isSupported(world, pos)) {
            world.breakBlock(pos, true);
            return finalState;
        }

        finalState = getShape(world, pos, finalState);
        if (!finalState.get(Properties.SHAPE).equals(Properties.Shape.ALL_WALLS)) {
            BlockState stateBelow = world.getBlockState(pos.down());
            if (stateBelow.getBlock() instanceof LumberStorageBlock) {
                finalState = finalState
                        .with(Properties.POSITION, Properties.VerticalPosition.TOP)
                        .with(Properties.SHAPE, stateBelow.get(Properties.SHAPE));
            } else {
                finalState = finalState.with(Properties.POSITION, Properties.VerticalPosition.BOTTOM);
            }
        }

        return finalState;
    }

    private static BlockState getShape(WorldAccess world, BlockPos pos, BlockState state) {
        Map<Direction, BlockState> surroundingStates = new HashMap<>();
        for (var direction : Direction.Type.HORIZONTAL) {
            surroundingStates.put(direction, world.getBlockState(pos.offset(direction)));
        }

        List<Direction> sameBlockDirections = surroundingStates.entrySet().stream()
                .filter(entry -> entry.getValue().getBlock() instanceof LumberStorageBlock)
                .map(Map.Entry::getKey).toList();

        switch (sameBlockDirections.size()) {
            case 0 -> state = state.with(Properties.SHAPE, Properties.Shape.ALL_WALLS);
            case 1 -> state = state
                    .with(Properties.SHAPE, Properties.Shape.TWO_WALLS_END)
                    .with(FACING, sameBlockDirections.getFirst());
            case 2 -> {
                if (wallsAreAdjacent(sameBlockDirections)) {
                    state = state.with(Properties.SHAPE, Properties.Shape.ONE_WALL_MID);
                } else {
                    state = state
                            .with(Properties.SHAPE, Properties.Shape.TWO_WALLS_MID)
                            .with(FACING, sameBlockDirections.getFirst());
                }
            }
            case 3 -> state = state.with(Properties.SHAPE, Properties.Shape.ONE_WALL_MID);
        }

        BlockState stateBelow = world.getBlockState(pos.down());
        if (stateBelow.getBlock() instanceof LumberStorageBlock) {
            state = state
                    .with(Properties.SHAPE, stateBelow.get(Properties.SHAPE))
                    .with(FACING, stateBelow.get(FACING))
                    .with(Properties.POSITION, Properties.VerticalPosition.TOP);
        }

        return state;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return LumberBlockVoxelShapes.get(state);
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isSupported(WorldView world, BlockPos originalPos) {
        BlockState stateBelow = world.getBlockState(originalPos.down());
        boolean belowIsSolid = stateBelow.isSideSolidFullSquare(world, originalPos.down(), Direction.UP);
        boolean hasSameBlockBelow = stateBelow.getBlock() instanceof LumberStorageBlock;
        return belowIsSolid || hasSameBlockBelow;
    }

    private int identicalHorizontalNeighborBlocks(BlockView world, BlockPos placedPos) {
        return (int) Direction.Type.HORIZONTAL.stream()
                .filter(direction -> world.getBlockState(placedPos.offset(direction)).getBlock() instanceof LumberStorageBlock)
                .count();
    }

    public boolean isNextToSameBlock(BlockView world, BlockPos placedPos) {
        return identicalHorizontalNeighborBlocks(world, placedPos) > 0;
    }

    public static boolean wallsAreAdjacent(List<Direction> sides) {
        return Direction.Type.HORIZONTAL.stream()
                .allMatch(direction -> sides.stream().noneMatch(direction.getOpposite()::equals));
    }

    public static int connectedBlocksCount(World world, BlockPos pos, HashSet<BlockPos> checked, List<Direction.Type> directionTypes) {
        if (checked.contains(pos)) return 0;
        int count = 1;
        for (var type : directionTypes) {
            Set<BlockPos> possibleDirections = type.stream()
                    .filter(direction -> world.getBlockState(pos.offset(direction)).getBlock() instanceof LumberStorageBlock)
                    .map(pos::offset)
                    .collect(Collectors.toSet());

            for (BlockPos newPos : possibleDirections) {
                count += connectedBlocksCount(world, newPos, checked, directionTypes);
            }
        }
        return count;
    }
}
