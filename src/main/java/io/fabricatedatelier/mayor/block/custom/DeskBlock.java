package io.fabricatedatelier.mayor.block.custom;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.block.entity.DeskBlockEntity;
import io.fabricatedatelier.mayor.config.MayorConfig;
import io.fabricatedatelier.mayor.network.packet.DeskCitizenScreenPacket;
import io.fabricatedatelier.mayor.network.packet.DeskMayorScreenPacket;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.CitizenHelper;
import io.fabricatedatelier.mayor.util.StateHelper;
import io.fabricatedatelier.mayor.util.StringUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
            if (!world.isClient()) {
                VillageData villageData = StateHelper.getClosestVillage((ServerWorld) world, pos);
                if (villageData != null) {
                    if (villageData.getCitizenData().getDeskPos() != null && villageData.getCitizenData().getDeskPos().equals(pos)) {
                        villageData.getCitizenData().setDeskPos(null);
                        StateHelper.getMayorVillageState((ServerWorld) world).markDirty();
                    }
                }
            }
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
        } else if (stack.isOf(Items.WRITABLE_BOOK)) {
            if (world.getBlockEntity(pos) instanceof DeskBlockEntity deskBlockEntity) {
                deskBlockEntity.setBook(stack.splitUnlessCreative(1, player));

                BlockState blockState = state.with(HAS_BOOK, true);
                world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockState));

                world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            return ItemActionResult.success(world.isClient());
        } else {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient() && state.get(HAS_BOOK)) {
            if (StateHelper.isInVillageRange((ServerWorld) world, pos)) {
                VillageData villageData = StateHelper.getClosestVillage((ServerWorld) world, pos);
                if (villageData != null) {
                    if (villageData.getCitizenData().getDeskPos() != null && villageData.getCitizenData().getDeskPos().equals(pos)) {
                        if (world.getBlockEntity(pos) instanceof DeskBlockEntity deskBlockEntity) {
                            deskBlockEntity.setValidated(true);
                        }
                        String mayorName = "";
                        if (villageData.getMayorPlayerUuid() != null) {
                            mayorName = StringUtil.getPlayerNameByUuid((ServerWorld) world, villageData.getMayorPlayerUuid());
                            if (villageData.getMayorPlayerUuid().equals(player.getUuid())) {
                                Map<UUID, String> registeredCitizens = new HashMap<>();
                                if (!villageData.getCitizenData().getCitizens().isEmpty()) {
                                    for (UUID uuid : villageData.getCitizenData().getCitizens()) {
                                        registeredCitizens.put(uuid, StringUtil.getPlayerNameByUuid((ServerWorld) world, uuid));
                                    }
                                }
                                Map<UUID, String> requestingCitizens = new HashMap<>();
                                if (!villageData.getCitizenData().getRequestCitizens().isEmpty()) {
                                    for (UUID uuid : villageData.getCitizenData().getRequestCitizens()) {
                                        requestingCitizens.put(uuid, StringUtil.getPlayerNameByUuid((ServerWorld) world, uuid));
                                    }
                                }
                                Map<UUID, String> taxPayedCitizens = new HashMap<>();
                                if (!villageData.getCitizenData().getTaxPayedCitizens().isEmpty()) {
                                    for (UUID uuid : villageData.getCitizenData().getTaxPayedCitizens()) {
                                        taxPayedCitizens.put(uuid, StringUtil.getPlayerNameByUuid((ServerWorld) world, uuid));
                                    }
                                }
                                new DeskMayorScreenPacket(pos, villageData.getName(), villageData.getLevel(), true, villageData.getCitizenData().getTaxAmount(), villageData.getCitizenData().getTaxInterval(),villageData.getCitizenData().getTaxTime(), villageData.getCitizenData().getRegistrationFee(), villageData.getVillagers().size(), villageData.getFunds(), MayorConfig.CONFIG.instance().villageFoundingCost, registeredCitizens, requestingCitizens, taxPayedCitizens).sendPacket((ServerPlayerEntity) player);
                                return ActionResult.success(true);
                            }
                        }
                        boolean citizen = CitizenHelper.isCitizenOfClosestVillage((ServerWorld) world, player) || player.isCreativeLevelTwoOp();
                        new DeskCitizenScreenPacket(pos, villageData.getName(), villageData.getLevel(), mayorName, citizen, villageData.getCitizenData().getTaxAmount(), villageData.getCitizenData().getTaxTime(), villageData.getCitizenData().getRegistrationFee(), villageData.getCitizenData().getCitizens().size(), villageData.getVillagers().size(), villageData.getFunds(), villageData.getCitizenData().getTaxPayedCitizens().contains(player.getUuid()), villageData.getCitizenData().getRequestCitizens().contains(player.getUuid())).sendPacket((ServerPlayerEntity) player);
                    } else if (world.getBlockEntity(pos) instanceof DeskBlockEntity deskBlockEntity) {
                        deskBlockEntity.setValidated(false);
                    }
                }
            } else if (world.getBlockEntity(pos) instanceof DeskBlockEntity deskBlockEntity) {
                deskBlockEntity.setValidated(false);
                // player.openHandledScreen(deskBlockEntity);

                if (StateHelper.isVillageTooClose((ServerWorld) world, pos)) {
                    player.sendMessage(Text.translatable("mayor.screen.desk.village_too_close"), true);
                    return ActionResult.success(false);
                }
                if (MayorConfig.CONFIG.instance().villageFoundingCost <= 0) {
                    player.sendMessage(Text.translatable("mayor.screen.desk.village_founding_disabled"), true);
                    return ActionResult.success(false);
                }
                new DeskMayorScreenPacket(pos, "", 0, false, 0, 0,0, 0, 0, 0, MayorConfig.CONFIG.instance().villageFoundingCost, new HashMap<>(), new HashMap<>(), new HashMap<>()).sendPacket((ServerPlayerEntity) player);
            }
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

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient()) {
            return;
        }
        if (!(world.getBlockEntity(pos) instanceof DeskBlockEntity deskBlockEntity)) {
            return;
        }
        VillageData villageData = StateHelper.getClosestVillage((ServerWorld) world, pos);
        if (villageData != null && villageData.getCitizenData().getDeskPos() == null) {
            if (villageData.getMayorPlayerUuid() != null) {
                if (placer instanceof PlayerEntity playerEntity && villageData.getMayorPlayerUuid().equals(playerEntity.getUuid())) {
                    deskBlockEntity.setValidated(true);
                    villageData.getCitizenData().setDeskPos(pos);
                }
            } else {
                deskBlockEntity.setValidated(true);
                villageData.getCitizenData().setDeskPos(pos);
            }
            world.updateListeners(pos, state, state, 0);
            StateHelper.getMayorVillageState((ServerWorld) world).markDirty();
        }
    }

}
