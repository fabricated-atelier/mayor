package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableMap;
import io.fabricatedatelier.mayor.entity.villager.access.Worker;
import io.fabricatedatelier.mayor.init.MayorVillagerUtilities;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class LumberjackTask extends MultiTickTask<VillagerEntity> {

    private static final int MAX_RUN_TIME = 6000;
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;

    public LumberjackTask() {
        super(ImmutableMap.of(MayorVillagerUtilities.BUSY, MemoryModuleState.VALUE_ABSENT), MAX_RUN_TIME * 2 / 3, MAX_RUN_TIME);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (serverWorld.getTime() < this.nextResponseTime) {
            return false;
        }
        if (villagerEntity instanceof Worker worker && worker.getVillageCenterPosition() != null && worker.hasTargetPosition()) {
            villagerEntity.getVillagerData().getLevel();
        }
        return this.currentTarget != null;
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if (this.currentTarget != null) {
            villagerEntity.getBrain().remember(MayorVillagerUtilities.BUSY, Unit.INSTANCE);
            villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));

//            if (villagerEntity instanceof Worker worker) {
//                worker.setTaskValue(2);
//
//                System.out.println("RUN BUILDER BREAK "+ worker.getTaskValue());
//            }
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
            TaskHelper.updateCarryItemStack(villagerEntity);
        }
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if (this.currentTarget != null) {
            if (this.currentTarget.getManhattanDistance(villagerEntity.getBlockPos()) <= 1 && villagerEntity instanceof Worker worker) {
                worker.setTaskValue(2);

                if (this.ticksRan % 20 == 0) {
//                    if (!StructureHelper.getObStructiveBlockMap(serverWorld, this.constructionData).isEmpty()) {
//                        boolean breakBlock = StructureHelper.breakBlock(serverWorld, this.constructionData, worker.getWorkerInventory());
//                        if (!breakBlock) {
//                            stop(serverWorld, villagerEntity, time);
//                        }
//                    } else {
//                        stop(serverWorld, villagerEntity, time);
//                    }
                }
            }
            // Maybe the builder will forget so this is the solution
            else if (!villagerEntity.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET) || !villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET).get().getLookTarget().getBlockPos().equals(this.currentTarget)) {
                villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
                villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
            }
            this.ticksRan++;
        }
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (villagerEntity instanceof Worker worker && !worker.hasTargetPosition()) {
            return false;
        }
        return this.ticksRan < MAX_RUN_TIME;
    }


}
