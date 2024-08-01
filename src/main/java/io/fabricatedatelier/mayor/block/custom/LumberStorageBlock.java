package io.fabricatedatelier.mayor.block.custom;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.block.entity.LumberStorageBlockEntity;
import io.fabricatedatelier.mayor.util.MayorProperties;
import io.fabricatedatelier.mayor.util.boilerplate.AbstractVillageContainerBlock;
import io.fabricatedatelier.mayor.util.voxelshape.LumberBlockVoxelShapes;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class LumberStorageBlock extends AbstractVillageContainerBlock {


    public LumberStorageBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(MayorProperties.SHAPE, MayorProperties.Shape.ALL_WALLS)
                .with(MayorProperties.POSITION, MayorProperties.VerticalPosition.BOTTOM)
                .with(MayorProperties.SIDE, MayorProperties.Side.RIGHT));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(MayorProperties.SHAPE, MayorProperties.POSITION, MayorProperties.SIDE);
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
        if (!isNextToSameBlock(world, pos)) setOrigin(world, pos);

        //TODO: use else branch if it exceeded the structure size
        if (targetedState.getBlock() instanceof LumberStorageBlock) {
            state = state.with(FACING, targetedState.get(FACING));
        } else {
            state = state.with(FACING, ctx.getHorizontalPlayerFacing());
        }

        if (world.getBlockState(pos.down()).getBlock() instanceof LumberStorageBlock) {
            state = state.with(MayorProperties.POSITION, MayorProperties.VerticalPosition.BOTTOM);
        } else if (world.getBlockState(pos.up()).getBlock() instanceof LumberStorageBlock) {
            state = state.with(MayorProperties.POSITION, MayorProperties.VerticalPosition.TOP);
        }

        return state;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        // setBlockState is still possible, use BlockItem for that
        if (isNextToSameBlock(world, pos) || !isSupported(world, pos)) return false;
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!isOrigin(world, pos)) return;

        for (BlockPos entry : BlockPos.iterateOutwards(pos, 1, 1, 1)) {
            BlockState entryState = world.getBlockState(entry);
            if (!(entryState.getBlock() instanceof LumberStorageBlock)) continue;
            setOrigin(world, entry);
            break;
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!isSupported(world, pos)) {
            world.breakBlock(pos, true);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
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

    public boolean wallsAreAdjacent(Map<Direction, Boolean> sides) {
        return Direction.Type.HORIZONTAL.stream().anyMatch(direction -> Arrays.stream(Direction.values())
                .filter(currentDirection -> currentDirection != direction.getOpposite()).anyMatch(sides::get));
    }

    public Optional<MayorProperties.Shape> getShape(BlockView world, BlockPos pos) {
        if (getOrigin(world, pos).isEmpty()) return Optional.empty();
        if (!(world.getBlockEntity(pos) instanceof LumberStorageBlockEntity blockEntity)) return Optional.empty();

        LinkedHashMap<Direction, Boolean> occupiedPositions = new LinkedHashMap<>();
        var horizontalDirections = Direction.Type.HORIZONTAL.stream().toList();
        for (Direction direction : horizontalDirections) {
            boolean isValidWall;
            BlockPos offset = pos.offset(direction);
            if (!(world.getBlockEntity(offset) instanceof LumberStorageBlockEntity offsetBlockEntity)) {
                isValidWall = false;
            } else {
                isValidWall = blockEntity.getStructureOriginPos().equals(offsetBlockEntity.getStructureOriginPos());
            }
            occupiedPositions.put(direction, isValidWall);
        }

        int neighborCount = (int) occupiedPositions.entrySet().stream().filter(Map.Entry::getValue).count();

        if (neighborCount == 0) return Optional.of(MayorProperties.Shape.ALL_WALLS);
        if (neighborCount == 1) return Optional.of(MayorProperties.Shape.TWO_WALLS_END);
        if (neighborCount == 2) {
            if (wallsAreAdjacent(occupiedPositions)) return Optional.of(MayorProperties.Shape.ONE_WALL_END);
            else return Optional.of(MayorProperties.Shape.TWO_WALLS_MID);
        }
        if (neighborCount == 3) return Optional.of(MayorProperties.Shape.ONE_WALL_MID);
        return Optional.empty();
    }


}
