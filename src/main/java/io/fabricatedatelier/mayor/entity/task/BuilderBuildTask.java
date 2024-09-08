package io.fabricatedatelier.mayor.entity.task;

import com.google.common.collect.ImmutableMap;
import io.fabricatedatelier.mayor.entity.access.Builder;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class BuilderBuildTask extends MultiTickTask<VillagerEntity> {
    //    private static final int MAX_RUN_TIME = 200;
//    public static final float WALK_SPEED = 0.5F;
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;
//    private final List<BlockPos> targetPositions = Lists.<BlockPos>newArrayList();

//    private final BlockPos targetPosition = BlockPos.ORIGIN;

    public BuilderBuildTask() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
//        if (!serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
//            return false;
//        } else
//            if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.FARMER) {
//            return false;
//        } else {
//            BlockPos.Mutable mutable = villagerEntity.getBlockPos().mutableCopy();
//            this.targetPositions.clear();
//
//            for (int i = -1; i <= 1; i++) {
//                for (int j = -1; j <= 1; j++) {
//                    for (int k = -1; k <= 1; k++) {
//                        mutable.set(villagerEntity.getX() + (double)i, villagerEntity.getY() + (double)j, villagerEntity.getZ() + (double)k);
//                        if (this.isSuitableTarget(mutable, serverWorld)) {
//                            this.targetPositions.add(new BlockPos(mutable));
//                        }
//                    }
//                }
//            }
        if (villagerEntity instanceof Builder builder && builder.getVillageCenterPosition() != null) {
            if (builder.getBuilderInventory().isEmpty()) {
                return false;
            }
            // Todo: Set closest building position

            this.currentTarget = findClosestTarget(serverWorld, villagerEntity, builder);
        }
        return this.currentTarget != null;
//        }
    }

    @Nullable
    private BlockPos findClosestTarget(ServerWorld serverWorld, VillagerEntity villagerEntity, Builder builder) {
        MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(serverWorld);
        if (mayorVillageState.getVillageData(builder.getVillageCenterPosition()) != null) {
            VillageData villageData = mayorVillageState.getVillageData(builder.getVillageCenterPosition());
            if (villageData.getConstructions().get(builder.getTargetPosition()) != null) {
                ConstructionData constructionData = villageData.getConstructions().get(builder.getTargetPosition());

                BlockPos pos = null;
                for (int i = constructionData.getStructureData().getBlockBox().getMinX(); i < constructionData.getStructureData().getBlockBox().getMaxX(); i++) {
                    for (int u = constructionData.getStructureData().getBlockBox().getMinZ(); u < constructionData.getStructureData().getBlockBox().getMaxZ(); u++) {
                        BlockPos checkPos = BlockPos.ofFloored(i, constructionData.getStructureData().getBlockBox().getMinY(), u);
                        if (pos != null && checkPos.getSquaredDistance(villagerEntity.getPos()) > pos.getSquaredDistance(villagerEntity.getPos())) {
                            continue;
                        }
                        pos = checkPos;
                    }
                }
                if (pos != null) {
                    for (int i = 0; i < 4; i++) {
                        if (!constructionData.getStructureData().getBlockBox().contains(pos.offset(Direction.fromHorizontal(i)))) {

                            // TEST Todo: Lol pos check
                            System.out.println("TEST " + serverWorld.getBlockState(pos.offset(Direction.fromHorizontal(i))));

                            return pos.offset(Direction.fromHorizontal(i));
                        }
                    }
                }

            }
        }
        return null;
    }


    //    @Nullable
//    private BlockPos chooseRandomTarget(ServerWorld world) {
//        return this.targetPositions.isEmpty() ? null : (BlockPos)this.targetPositions.get(world.getRandom().nextInt(this.targetPositions.size()));
//    }
//
//    private boolean isSuitableTarget(BlockPos pos, ServerWorld world) {
//        BlockState blockState = world.getBlockState(pos);
//        Block block = blockState.getBlock();
//        Block block2 = world.getBlockState(pos.down()).getBlock();
//        return block instanceof CropBlock && ((CropBlock)block).isMature(blockState) || blockState.isAir() && block2 instanceof FarmlandBlock;
//    }
    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (l > this.nextResponseTime && this.currentTarget != null) {
            villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = l + 40L;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (this.currentTarget == null || this.currentTarget.isWithinDistance(villagerEntity.getPos(), 1.0)) {
            if (this.currentTarget != null && l > this.nextResponseTime) {
                BlockState blockState = serverWorld.getBlockState(this.currentTarget);
//                Block block = blockState.getBlock();
//                Block block2 = serverWorld.getBlockState(this.currentTarget.down()).getBlock();
//                BlockEntities
                System.out.println("KEEP RUNNING " + villagerEntity + " : " + l);
//                if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState)) {
//                    serverWorld.breakBlock(this.currentTarget, true, villagerEntity);
//                }

//                if (blockState.isAir() && block2 instanceof FarmlandBlock && villagerEntity.hasSeedToPlant()) {
//                    SimpleInventory simpleInventory = villagerEntity.getInventory();
//
//                    for (int i = 0; i < simpleInventory.size(); i++) {
//                        ItemStack itemStack = simpleInventory.getStack(i);
//                        boolean bl = false;
//                        if (!itemStack.isEmpty() && itemStack.isIn(ItemTags.VILLAGER_PLANTABLE_SEEDS) && itemStack.getItem() instanceof BlockItem blockItem) {
//                            BlockState blockState2 = blockItem.getBlock().getDefaultState();
//                            serverWorld.setBlockState(this.currentTarget, blockState2);
//                            serverWorld.emitGameEvent(GameEvent.BLOCK_PLACE, this.currentTarget, GameEvent.Emitter.of(villagerEntity, blockState2));
//                            bl = true;
//                        }
//
//                        if (bl) {
//                            serverWorld.playSound(
//                                    null,
//                                    (double)this.currentTarget.getX(),
//                                    (double)this.currentTarget.getY(),
//                                    (double)this.currentTarget.getZ(),
//                                    SoundEvents.ITEM_CROP_PLANT,
//                                    SoundCategory.BLOCKS,
//                                    1.0F,
//                                    1.0F
//                            );
//                            itemStack.decrement(1);
//                            if (itemStack.isEmpty()) {
//                                simpleInventory.setStack(i, ItemStack.EMPTY);
//                            }
//                            break;
//                        }
//                    }
//                }

//                if (block instanceof CropBlock && !((CropBlock)block).isMature(blockState)) {
//                    this.targetPositions.remove(this.currentTarget);
//                    this.currentTarget = this.chooseRandomTarget(serverWorld);
//                    if (this.currentTarget != null) {
//                        this.nextResponseTime = l + 20L;
//                        villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
//                        villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
//                    }
//                }
            }

            this.ticksRan++;
        }
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (villagerEntity instanceof Builder builder && builder.getBuilderInventory().isEmpty()) {
            return false;
        }
        return this.ticksRan < 2000;
    }
}

