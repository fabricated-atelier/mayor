package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableMap;
import io.fabricatedatelier.mayor.block.entity.VillageContainerBlockEntity;
import io.fabricatedatelier.mayor.entity.villager.access.Worker;
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
        super(ImmutableMap.of(MayorVillagerUtilities.BUSY, MemoryModuleState.VALUE_ABSENT), MAX_RUN_TIME * 2 / 3, MAX_RUN_TIME);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (serverWorld.getTime() < this.nextResponseTime) {
            return false;
        }
        if (villagerEntity instanceof Worker worker) {
            if (worker.getWorkerInventory().isEmpty()) {
                return false;
            }
            if (worker.getVillageCenterPosition() != null) {
                VillageState villageState = StateHelper.getMayorVillageState(serverWorld);
                if (villageState.getVillageData(worker.getVillageCenterPosition()) != null) {
                    VillageData villageData = villageState.getVillageData(worker.getVillageCenterPosition());
                    if (worker.hasTargetPosition() && villageData.getConstructions().containsKey(worker.getTargetPosition())) {
                        ConstructionData constructionData = villageData.getConstructions().get(worker.getTargetPosition());

                        if (constructionData.getDemolish() || !StructureHelper.hasMissingConstructionItem(serverWorld, constructionData, worker.getWorkerInventory())) {
                            this.currentTarget = getTarget(serverWorld, worker, villageData);
                        } else {
                            System.out.println("DUMP NOT?");
                            this.nextResponseTime = serverWorld.getTime() + 100L;
                        }
                    } else {
                        this.currentTarget = getTarget(serverWorld, worker, villageData);
                    }
                }
                return this.currentTarget != null;
            }
        }
        return false;
    }

    @Nullable
    private BlockPos getTarget(ServerWorld serverWorld, Worker worker, VillageData villageData) {
        if (villageData.getStorageOriginBlockPosList().size() <= 0 || worker.getWorkerInventory().isEmpty()) {
            return null;
        }
        ItemStack stack = worker.getWorkerInventory().getFirstStack();
        for (int i = 0; i < villageData.getStorageOriginBlockPosList().size(); i++) {
            if (serverWorld.getBlockEntity(villageData.getStorageOriginBlockPosList().get(i)) instanceof VillageContainerBlockEntity villageContainerBlockEntity) {
                if (!villageContainerBlockEntity.isFull(stack)) {
                    if (villageContainerBlockEntity.getStructureOriginPos().isPresent() && TaskHelper.canReachSite(worker.getVillagerEntity(), villageContainerBlockEntity.getStructureOriginPos().get())) {
                        return villageContainerBlockEntity.getStructureOriginPos().get();
                    } else {
                        for (BlockPos pos : villageContainerBlockEntity.getConnectedBlocks()) {
                            if (TaskHelper.canReachSite(worker.getVillagerEntity(), pos)) {
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

        // Edge case
        if (villagerEntity instanceof Worker worker && worker.hasTargetPosition()) {
            VillageData villageData = StateHelper.getMayorVillageState(serverWorld).getVillageData(worker.getVillageCenterPosition());
            if (villageData != null && villageData.getConstructions().containsKey(worker.getTargetPosition())) {
                ConstructionData constructionData = villageData.getConstructions().get(worker.getTargetPosition());
                if (constructionData.getDemolish()) {
                    if (StructureHelper.getObStructiveBlockMap(serverWorld, constructionData).isEmpty()) {
                        BuilderBreakTask.finishDemolishTask(serverWorld, villagerEntity, time);
                    }
                } else if (StructureHelper.getMissingConstructionBlockMap(serverWorld, constructionData).isEmpty()) {
                    BuilderBuildTask.finishBuildTask(serverWorld, villagerEntity, time);
                }
            }
        }
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {

//        System.out.println("?X");

        if (this.currentTarget != null) {
            if (this.currentTarget.getManhattanDistance(villagerEntity.getBlockPos()) <= 2) {
                if (serverWorld.getBlockEntity(this.currentTarget) instanceof VillageContainerBlockEntity containerBlockEntity && containerBlockEntity.getStructureOriginBlockEntity().isPresent() && villagerEntity instanceof Worker worker) {
                    VillageData villageData = StateHelper.getMayorVillageState(serverWorld).getVillageData(worker.getVillageCenterPosition());
                    if (villageData != null) {
                        VillageContainerBlockEntity villageContainerBlockEntity = containerBlockEntity.getStructureOriginBlockEntity().get();
                        for (ItemStack stack : worker.getWorkerInventory().getHeldStacks()) {
                            if (villageContainerBlockEntity.tryAddingStack(stack.copy())) {
                                stack.decrement(stack.getCount());
                            }
                        }
//                      villageContainerBlockEntity.markDirty(); already done at tryAddingStack method
                        serverWorld.updateListeners(villageContainerBlockEntity.getPos(), villageContainerBlockEntity.getCachedState(), villageContainerBlockEntity.getCachedState(), 0);

                        if (!worker.getWorkerInventory().isEmpty()) {
                            this.currentTarget = this.getTarget(serverWorld, worker, villageData);
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
        if (villagerEntity instanceof Worker worker && worker.getWorkerInventory().isEmpty()) {
            return false;
        }
        return this.ticksRan < MAX_RUN_TIME;
    }
}


