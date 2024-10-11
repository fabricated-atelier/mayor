package io.fabricatedatelier.mayor.block.custom;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.block.entity.DeskBlockEntity;
import io.fabricatedatelier.mayor.util.CitizenHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DeskBlock extends BlockWithEntity {

    public static final MapCodec<DeskBlock> CODEC = createCodec(DeskBlock::new);
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty HAS_BOOK = Properties.HAS_BOOK;

    private static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    private static final VoxelShape MIDDLE_SHAPE = Block.createCuboidShape(4.0, 2.0, 4.0, 12.0, 12.0, 12.0);
    private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);

    public static final VoxelShape FULL_SHAPE = VoxelShapes.union(BOTTOM_SHAPE, MIDDLE_SHAPE, TOP_SHAPE);

    public DeskBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HAS_BOOK, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DeskBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return FULL_SHAPE;
    }

    @Override
    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        ItemStack itemStack = ctx.getStack();
        PlayerEntity playerEntity = ctx.getPlayer();
        boolean bl = false;
        if (!world.isClient() && playerEntity != null && playerEntity.isCreativeLevelTwoOp()) {
            NbtComponent nbtComponent = itemStack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT);
            if (nbtComponent.contains("Book")) {
                bl = true;
            }
        }

        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(HAS_BOOK, bl);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return FULL_SHAPE;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return FULL_SHAPE;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_BOOK);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (state.get(HAS_BOOK) && world.getBlockEntity(pos) instanceof DeskBlockEntity deskBlockEntity) {
                Direction direction = state.get(FACING);
                ItemStack itemStack = deskBlockEntity.getBook().copy();
                float f = 0.25F * (float) direction.getOffsetX();
                float g = 0.25F * (float) direction.getOffsetZ();
                ItemEntity itemEntity = new ItemEntity(
                        world, (double) pos.getX() + 0.5 + (double) f, (pos.getY() + 1), (double) pos.getZ() + 0.5 + (double) g, itemStack
                );
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
                deskBlockEntity.clear();
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(HAS_BOOK)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else if (stack.isIn(ItemTags.LECTERN_BOOKS)) {
            if (world.getBlockEntity(pos) instanceof DeskBlockEntity deskBlockEntity) {
                deskBlockEntity.setBook(stack.splitUnlessCreative(1, player));

                BlockState blockState = state.with(HAS_BOOK, true);
                world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockState));

                world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return ItemActionResult.success(world.isClient());
        } else {
            return stack.isEmpty() && hand == Hand.MAIN_HAND ? ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            if (!state.get(HAS_BOOK) && !CitizenHelper.isCitizenOfClosestVillage((ServerWorld) world, player)) {
                return ActionResult.CONSUME;
            }
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DeskBlockEntity deskBlockEntity) {

                // Todo: WORK HERE
                //RawFilteredPair<String> title, String author, int generation, List<RawFilteredPair<Text>> pages, boolean resolved

                WrittenBookContentComponent writtenBookContentComponent = deskBlockEntity.getBook().get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
                if (writtenBookContentComponent == null) {
                    List<Text> test = new ArrayList<>();
                    test.add(Text.of("Expenses         Value House                   1 Big House             20"));
                    List<RawFilteredPair<Text>> list = new ArrayList<>();//test.stream().map(page-> RawFilteredPair.of(page).map(Text::literal)).toList();
                    test.stream().forEach(page -> list.add(RawFilteredPair.of(page)));

                    writtenBookContentComponent = new WrittenBookContentComponent(RawFilteredPair.of("Crash Book"), "Village", 0, list, false);
//                        List<RawFilteredPair<Text>> list = pages.stream().map(page -> this.toRawFilteredPair(page).map(Text::literal)).toList();
                    deskBlockEntity.getBook().set(DataComponentTypes.WRITTEN_BOOK_CONTENT, writtenBookContentComponent);
                } else {

//                        pages=[RawFilteredPair[raw=literal{Expenses         Value
//
//                            House                   1
//                            Big House             20}, filtered=Optional.empty]]
// 22
                    System.out.println(writtenBookContentComponent.getPages(false));
                }
                System.out.println(writtenBookContentComponent);

                player.openHandledScreen(deskBlockEntity);
            }
        } else {
            // Max Width: 112
//                MinecraftClient client = MinecraftClient.getInstance();
//                System.out.println(client.textRenderer.getWidth(Text.of("Expenses         Value"))+ " : "+client.textRenderer.getWidth(Text.of("House                   1"))+ " : "+client.textRenderer.getWidth(Text.of("Big House             20")));
        }

        return ActionResult.success(world.isClient());

    }

    @Nullable
    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return super.createScreenHandlerFactory(state, world, pos);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}
