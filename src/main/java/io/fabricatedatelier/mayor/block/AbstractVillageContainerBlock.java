package io.fabricatedatelier.mayor.block;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.block.entity.VillageContainerBlockEntity;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.CitizenHelper;
import io.fabricatedatelier.mayor.util.ConnectedBlockUtil;
import io.fabricatedatelier.mayor.util.StateHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockPos originPos = getOrigin(world, pos).orElse(pos);
        if (world.getBlockEntity(originPos) instanceof VillageContainerBlockEntity blockEntity && !world.isClient()) {
            VillageData villageData = StateHelper.getClosestVillage((ServerWorld) world, pos);
            if (villageData != null && villageData.getStorageOriginBlockPosList().contains(originPos) && !CitizenHelper.isCitizenOfClosestVillage((ServerWorld) world, player)) {
                player.sendMessage(Text.translatable("mayor.village.citizen.unregistered"), true);
                return ActionResult.FAIL;
            }
            // extract
            Optional<ItemStack> removedStack = blockEntity.extractFromOrigin(hit.getSide());
            if (removedStack.isPresent() && !removedStack.get().isEmpty()) {
                player.getInventory().offerOrDrop(removedStack.get());
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockPos originPos = getOrigin(world, pos).orElse(pos);

        if (world.isClient())
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        if (!(world.getBlockEntity(originPos) instanceof VillageContainerBlockEntity blockEntity))
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);

        if (blockEntity.canInsert(player.getStackInHand(hand).copy(), hit.getSide())) {
            VillageData villageData = StateHelper.getClosestVillage((ServerWorld) world, pos);
            if (villageData != null && villageData.getStorageOriginBlockPosList().contains(originPos) && !CitizenHelper.isCitizenOfClosestVillage((ServerWorld) world, player)) {
                player.sendMessage(Text.translatable("mayor.village.citizen.unregistered"), true);
                return ItemActionResult.FAIL;
            }
            if (blockEntity.insertIntoOrigin(player.getStackInHand(hand).copy(), hit.getSide())) {
                player.getStackInHand(hand).decrementUnlessCreative(player.getStackInHand(hand).getCount(), player);
                return ItemActionResult.SUCCESS;
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
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
            state = state.with(POSITION, getPositionFromConnectedWalls(world, pos));
            world.setBlockState(pos, state);
        }

        if (!(world.getBlockEntity(pos) instanceof VillageContainerBlockEntity blockEntity)) {
            super.onPlaced(world, pos, state, placer, itemStack);
            return;
        }

        Optional<BlockPos> neighborPos = getFirstConnectedBlock(world, pos);
        if (neighborPos.isEmpty()) {
            blockEntity.setStructureOriginPos(getOrigin(world, pos).orElse(pos));
        } else if (world.getBlockEntity(neighborPos.get()) instanceof VillageContainerBlockEntity neighborBlockEntity) {
            if (neighborBlockEntity.getStructureOriginPos().isPresent()) {
                blockEntity.setStructureOriginPos(neighborBlockEntity.getStructureOriginPos().get());
                if (world.getBlockEntity(neighborBlockEntity.getStructureOriginPos().get()) instanceof VillageContainerBlockEntity originBlockEntity) {
                    var originBox = new ConnectedBlockUtil.BoundingBox(world, neighborBlockEntity.getStructureOriginPos().get(), false);
                    originBlockEntity.clearConnectedBlocks();
                    originBlockEntity.addConnectedBlocks(new ArrayList<>(originBox.getConnectedPosList()));
                }
            }
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
            state = state.with(POSITION, getPositionFromConnectedWalls(world, pos));
        } else {
            state = state.with(POSITION, MayorProperties.Position.SINGLE);
        }
        return state;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            return;
        }
        if (world.getBlockEntity(pos) instanceof VillageContainerBlockEntity blockEntity) {
            if (blockEntity.isStructureOrigin()) {
                if (getFirstConnectedBlock(world, pos).isPresent()) {
                    BlockPos firstConnectedPos = getFirstConnectedBlock(world, pos).get();
                    blockEntity.broadcastNewOriginPos(world, firstConnectedPos);
                    if (world.getBlockEntity(firstConnectedPos) instanceof VillageContainerBlockEntity newOriginBlockEntity) {
                        blockEntity.moveInventory(newOriginBlockEntity);
                        blockEntity.moveConnectedBlocks(newOriginBlockEntity);
                    }
                } else {
                    if (!world.isClient()) {
                        VillageData villageData = StateHelper.getClosestVillage((ServerWorld) world, pos);
                        if (villageData != null) {
                            villageData.removeStorageOriginBlockPos(pos);
                        }
                        if (!blockEntity.isEmpty()) {
                            ItemScatterer.spawn(world, pos, blockEntity);
                        }
                    }
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
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
        return VoxelShapes.empty();
    }

    @Override
    abstract public MapCodec<? extends BlockWithEntity> getCodec();

    @Nullable
    @Override
    abstract public BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    public static MayorProperties.Position getPositionFromConnectedWalls(WorldAccess world, BlockPos pos) {
        HashSet<Direction> connectedBlocks = getValidConnectedDirections(world, pos);
        for (var entry : MayorProperties.Position.values()) {
            if (entry.getConnectedDirections().equals(connectedBlocks)) return entry;
        }
        return MayorProperties.Position.SINGLE;
    }

    private static boolean isSameBlock(BlockState stateA, BlockState stateB) {
        return stateA.getBlock().equals(stateB.getBlock());
    }

    public static Map<Direction, BlockState> getConnectedBlockStates(WorldAccess world, BlockPos pos) {
        Map<Direction, BlockState> connectedBlockStates = new HashMap<>();
        for (Direction entry : Direction.Type.HORIZONTAL) {
            connectedBlockStates.put(entry, world.getBlockState(pos.offset(entry)));
        }
        return connectedBlockStates;
    }

    public static Optional<BlockPos> getFirstConnectedBlock(WorldAccess world, BlockPos pos) {
        return getConnectedBlockStates(world, pos).entrySet()
                .stream().filter(entry -> entry.getValue().getBlock() instanceof AbstractVillageContainerBlock)
                .findFirst().map(entry -> pos.offset(entry.getKey()));
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
        if (!(world.getBlockEntity(pos) instanceof VillageContainerBlockEntity blockEntity)) return;
        blockEntity.setStructureOriginPos(pos);
    }

    public static Optional<BlockPos> getOrigin(BlockView world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof VillageContainerBlockEntity blockEntity))
            return Optional.empty();
        return blockEntity.getStructureOriginPos();
    }
}
