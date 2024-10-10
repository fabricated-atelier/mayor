package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableMap;
import io.fabricatedatelier.mayor.block.entity.VillageContainerBlockEntity;
import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.init.MayorVillagerUtilities;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.VillageState;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.StateHelper;
import io.fabricatedatelier.mayor.util.StructureHelper;
import io.fabricatedatelier.mayor.util.TaskHelper;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BuilderDumpTask extends MultiTickTask<VillagerEntity> {

    private static final int MAX_RUN_TIME = 6000;
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;

    public BuilderDumpTask() {
        //   super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
        // super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
        super(ImmutableMap.of(MayorVillagerUtilities.BUSY, MemoryModuleState.VALUE_ABSENT), MAX_RUN_TIME * 2 / 3, MAX_RUN_TIME);
    }

    @Override
    protected boolean hasRequiredMemoryState(VillagerEntity entity) {

        boolean test = super.hasRequiredMemoryState(entity);
//        System.out.println("X "+test+ " : "+entity.getBrain().getOptionalMemory(MayorVillagerUtilities.BUSY));
        return test;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
//
//        System.out.println("?");

        if (serverWorld.getTime() < this.nextResponseTime) {
            return false;
        }
        if (villagerEntity instanceof Builder builder) {
            if (builder.getBuilderInventory().isEmpty()) {
                return false;
            }
            if (builder.getVillageCenterPosition() != null) {
                VillageState villageState = StateHelper.getMayorVillageState(serverWorld);
                if (villageState.getVillageData(builder.getVillageCenterPosition()) != null) {
                    VillageData villageData = villageState.getVillageData(builder.getVillageCenterPosition());
                    if (builder.hasTargetPosition()) {
                        if (villageData.getConstructions().containsKey(builder.getTargetPosition())) {
                            ConstructionData constructionData = villageData.getConstructions().get(builder.getTargetPosition());

//                            if (StructureHelper.getMissingConstructionBlockMap(serverWorld, constructionData).isEmpty()) {
                            if (!StructureHelper.hasMissingConstructionItem(serverWorld, constructionData, builder.getBuilderInventory())) {
                                this.currentTarget = getTarget(serverWorld, builder, villageData);
                            } else {
                                System.out.println("DUMP NOT?");
                                this.nextResponseTime = serverWorld.getTime() + 100L;
                            }
                        }
                    } else {
                        this.currentTarget = getTarget(serverWorld, builder, villageData);
                    }
                }
                return this.currentTarget != null;
            }
        }
        return false;
    }

    @Nullable
    private BlockPos getTarget(ServerWorld serverWorld, Builder builder, VillageData villageData) {
        if (villageData.getStorageOriginBlockPosList().size() <= 0 || builder.getBuilderInventory().isEmpty()) {
            return null;
        }
        ItemStack stack = builder.getBuilderInventory().getFirstStack();
        for (int i = 0; i < villageData.getStorageOriginBlockPosList().size(); i++) {
            if (serverWorld.getBlockEntity(villageData.getStorageOriginBlockPosList().get(i)) instanceof VillageContainerBlockEntity villageContainerBlockEntity) {
                if (!villageContainerBlockEntity.isFull(stack)) {
                    if (villageContainerBlockEntity.getStructureOriginPos().isPresent() && TaskHelper.canReachSite(builder.getVillagerEntity(), villageContainerBlockEntity.getStructureOriginPos().get())) {
                        return villageContainerBlockEntity.getStructureOriginPos().get();
                    } else {
                        for (BlockPos pos : villageContainerBlockEntity.getConnectedBlocks()) {
                            if (TaskHelper.canReachSite(builder.getVillagerEntity(), pos)) {
                                return pos;
                            }
                        }
                    }
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if (this.currentTarget != null) {
            villagerEntity.getBrain().remember(MayorVillagerUtilities.BUSY, Unit.INSTANCE);
            villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.7F, 1));

            System.out.println("RUN BUILDER DUMP");
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);

        this.ticksRan = 0;
        this.currentTarget = null;
        this.nextResponseTime = time + 60L;
        villagerEntity.getBrain().forget(MayorVillagerUtilities.BUSY);
        System.out.println("FINISH BUILDER DUMP");

        TaskHelper.updateCarryItemStack(villagerEntity);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {

//        System.out.println("?X");

        if (this.currentTarget != null) {
            if (this.currentTarget.getManhattanDistance(villagerEntity.getBlockPos()) <= 1) {
                if (serverWorld.getBlockEntity(this.currentTarget) instanceof VillageContainerBlockEntity containerBlockEntity && containerBlockEntity.getStructureOriginBlockEntity().isPresent() && villagerEntity instanceof Builder builder) {
                    VillageData villageData = StateHelper.getMayorVillageState(serverWorld).getVillageData(builder.getVillageCenterPosition());
                    if (villageData != null) {
                        VillageContainerBlockEntity villageContainerBlockEntity = containerBlockEntity.getStructureOriginBlockEntity().get();
                        for (ItemStack stack : builder.getBuilderInventory().getHeldStacks()) {
                            if (villageContainerBlockEntity.tryAddingStack(stack.copy())) {
                                stack.decrement(stack.getCount());
                            }
                        }
//                      villageContainerBlockEntity.markDirty(); already done at tryAddingStack method
                        serverWorld.updateListeners(villageContainerBlockEntity.getPos(), villageContainerBlockEntity.getCachedState(), villageContainerBlockEntity.getCachedState(), 0);

                        if (!builder.getBuilderInventory().isEmpty()) {
                            this.currentTarget = this.getTarget(serverWorld, builder, villageData);
                            if (this.currentTarget != null) {
                                villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
                                villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.7F, 1));
                            } else {
                                stop(serverWorld, villagerEntity, serverWorld.getTime());
                            }
                        }
                    } else {
                        stop(serverWorld, villagerEntity, serverWorld.getTime());
                    }
                }
            }
            this.ticksRan++;
        }

    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (villagerEntity instanceof Builder builder && builder.getBuilderInventory().isEmpty()) {
            return false;
        }
        return this.ticksRan < MAX_RUN_TIME;
    }
}


