package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableMap;
import io.fabricatedatelier.mayor.entity.villager.access.Worker;
import io.fabricatedatelier.mayor.init.MayorVillagerUtilities;
import io.fabricatedatelier.mayor.util.TaskHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LumberjackTask extends MultiTickTask<VillagerEntity> {

    private static final int MAX_RUN_TIME = 6000;
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;

    private List<BlockPos> cachedTreeBottomPositions = new ArrayList<>();

    public LumberjackTask() {
        super(ImmutableMap.of(MayorVillagerUtilities.BUSY, MemoryModuleState.VALUE_ABSENT), MAX_RUN_TIME * 2 / 3, MAX_RUN_TIME);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (serverWorld.getTime() < this.nextResponseTime) {
            return false;
        }
        if (villagerEntity instanceof Worker worker && worker.getVillageCenterPosition() != null && worker.hasTargetPosition() && !worker.getWorkerInventory().isInventoryAlmostFull()) {
            if (!this.cachedTreeBottomPositions.isEmpty()) {
                for (BlockPos cachedTreeBottomPosition : this.cachedTreeBottomPositions) {
                    if (TaskHelper.canReachSite(villagerEntity, cachedTreeBottomPosition)) {
                        this.currentTarget = cachedTreeBottomPosition;
                        break;
                    }
                }
            } else {
                this.nextResponseTime = serverWorld.getTime() + 400L;
                findTreesInArea(serverWorld, villagerEntity);
            }
        }
        return this.currentTarget != null;
    }

    public void findTreesInArea(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (villagerEntity instanceof Worker worker && worker.getVillageCenterPosition() != null && worker.hasTargetPosition()) {
            int radius = villagerEntity.getVillagerData().getLevel() * 5 + 5;

            CompletableFuture.supplyAsync(() -> {
                        List<BlockPos> treeBottomPositions = new ArrayList<>();

                        int x = worker.getTargetPosition().getX();
                        int y = worker.getTargetPosition().getY();
                        int z = worker.getTargetPosition().getZ();
                        for (int i = x - radius; i < x + radius; i++) {
                            for (int u = z - radius; u < z + radius; u++) {
                                BlockPos pos = serverWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(i, y, u));
                                if (serverWorld.getBlockState(pos).isIn(BlockTags.LEAVES)
                                        && serverWorld.getBlockState(pos.down()).isIn(BlockTags.LOGS)
                                        && serverWorld.getBlockState(pos.down(2)).isIn(BlockTags.LOGS)
                                        && serverWorld.getBlockState(pos.down(3)).isIn(BlockTags.LOGS)) {
                                    for (int o = 4; o < 35; o++) {
                                        if (!serverWorld.getBlockState(pos.down(o)).isIn(BlockTags.LOGS)) {
                                            if (!treeBottomPositions.contains(pos.down(o - 1))) {
                                                treeBottomPositions.add(pos.down(o - 1));
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        return treeBottomPositions;
                    })
                    .thenAccept(list -> {
                        this.cachedTreeBottomPositions.clear();
                        this.cachedTreeBottomPositions.addAll(list);
                    });
        }
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if (this.currentTarget != null) {
            villagerEntity.getBrain().remember(MayorVillagerUtilities.BUSY, Unit.INSTANCE);
            villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget.up()));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
        }
    }


    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = time + 100L;
        this.currentTarget = null;
        villagerEntity.getBrain().forget(MayorVillagerUtilities.BUSY);

        if (villagerEntity instanceof Worker worker) {
            worker.setTaskValue(0);
//            TaskHelper.updateCarryItemStack(villagerEntity);
        }
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if (this.currentTarget != null && villagerEntity instanceof Worker worker) {

            if (this.ticksRan % 3 == 0) {
                TaskHelper.pickUpItems(serverWorld, villagerEntity, ItemTags.LOGS, ItemTags.SAPLINGS);
            }
            if (this.currentTarget.getManhattanDistance(villagerEntity.getBlockPos()) <= 2) {
                worker.setTaskValue(3);

                if (this.ticksRan % 20 == 0) {
                    if (!serverWorld.getBlockState(this.currentTarget).isAir()) {
                        if (serverWorld.getBlockState(this.currentTarget).isIn(BlockTags.LOGS)) {
                            // cut tree
                            for (int o = 1; o < 35; o++) {
                                if (!serverWorld.getBlockState(this.currentTarget.up(o)).isIn(BlockTags.LOGS)) {
                                    serverWorld.breakBlock(this.currentTarget.up(o - 1), true);
                                    break;
                                }
                            }

                            villagerEntity.swingHand(Hand.MAIN_HAND, false);
                        } else {
                            goToNextTree(serverWorld, villagerEntity, time);
                        }
                    } else {
                        // plant sapling
                        if (worker.getWorkerInventory().containsAny(stack -> stack.isIn(ItemTags.SAPLINGS))) {
                            if (serverWorld.getBlockState(this.currentTarget.down()).isIn(BlockTags.DIRT)) {
                                ItemStack saplingStack = ItemStack.EMPTY;
                                for (ItemStack stack : worker.getWorkerInventory().getHeldStacks()) {
                                    if (stack.isIn(ItemTags.SAPLINGS) && stack.getItem() instanceof BlockItem) {
                                        saplingStack = stack;
                                        break;
                                    }
                                }
                                if (!saplingStack.isEmpty() && saplingStack.getItem() instanceof BlockItem blockItem) {
                                    serverWorld.setBlockState(this.currentTarget, blockItem.getBlock().getDefaultState());
                                    BlockState state = serverWorld.getBlockState(this.currentTarget);
                                    BlockSoundGroup blockSoundGroup = state.getSoundGroup();
                                    serverWorld.playSound(null, this.currentTarget, state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);

                                    saplingStack.decrement(1);
                                    villagerEntity.swingHand(Hand.MAIN_HAND, false);
                                }
                            }
                        } else {
                            // Todo: What to do if no sapling in inventory but should place one?
                        }

                        goToNextTree(serverWorld, villagerEntity, time);
                    }
                }
            }
            // Maybe the lumberjack will forget so this is the solution
            else if (!villagerEntity.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET) || !villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET).get().getLookTarget().getBlockPos().equals(this.currentTarget)) {
                villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget.up()));
                villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
            }
            this.ticksRan++;
        }
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if (villagerEntity instanceof Worker worker && (!worker.hasTargetPosition() || this.cachedTreeBottomPositions.isEmpty() || worker.getWorkerInventory().isInventoryAlmostFull())) {
            return false;
        }
        return this.ticksRan < MAX_RUN_TIME;
    }

    // Todo: issue with big trees and logs not directly above stem
    // Maybe search for item entities in work area and collect them
    public void goToNextTree(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        this.cachedTreeBottomPositions.remove(this.currentTarget);

        if (!this.cachedTreeBottomPositions.isEmpty() && villagerEntity instanceof Worker worker && !worker.getWorkerInventory().isInventoryAlmostFull()) {
            this.currentTarget = this.cachedTreeBottomPositions.getFirst();

            villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget.up()));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
        } else {
            stop(serverWorld, villagerEntity, time);
            // start dump here, should already work
        }
    }

}
