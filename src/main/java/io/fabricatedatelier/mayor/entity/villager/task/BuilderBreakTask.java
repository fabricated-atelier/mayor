package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableMap;
import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.init.MayorVillagerUtilities;
import io.fabricatedatelier.mayor.state.ConstructionData;
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

public class BuilderBreakTask extends MultiTickTask<VillagerEntity> {

    private static final int MAX_RUN_TIME = 6000;
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;

    @Nullable
    private ConstructionData constructionData = null;

    public BuilderBreakTask() {
        super(ImmutableMap.of(MayorVillagerUtilities.BUSY, MemoryModuleState.VALUE_ABSENT), MAX_RUN_TIME * 2 / 3, MAX_RUN_TIME);
    }

//    public boolean isTempted() {
//        return (Boolean)this.brain.getOptionalRegisteredMemory(MemoryModuleType.IS_TEMPTED).orElse(false);
//    }


//    @Override
//    protected boolean hasRequiredMemoryState(VillagerEntity entity) {
//        boolean test = super.hasRequiredMemoryState(entity);
//        System.out.println("HAS BREAK MEMORY STATE: "+test+ " : "+entity.getBrain().getOptionalMemory(MayorVillagerUtilities.SHOULD_DUMP)+ " : "+entity.getBrain().getOptionalMemory(MayorVillagerUtilities.SHOULD_BREAK));
//        return test;
//    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (serverWorld.getTime() < this.nextResponseTime) {
            return false;
        }
        if (villagerEntity instanceof Builder builder && builder.getVillageCenterPosition() != null && builder.hasTargetPosition()) {
            if (this.constructionData == null) {
                if (StateHelper.getMayorVillageState(serverWorld) != null) {
                    VillageData villageData = StateHelper.getMayorVillageState(serverWorld).getVillageData(builder.getVillageCenterPosition());
                    if (villageData != null) {
                        if (!villageData.getConstructions().isEmpty() && villageData.getConstructions().containsKey(builder.getTargetPosition()) && !StructureHelper.getObStructiveBlockMap(serverWorld, villageData.getConstructions().get(builder.getTargetPosition())).isEmpty()) {
                            this.constructionData = villageData.getConstructions().get(builder.getTargetPosition());
                        } else {
                            this.nextResponseTime = serverWorld.getTime() + 100L;
                        }
                    }
                }
            }
            if (this.constructionData != null) {
                this.currentTarget = TaskHelper.findClosestTarget(serverWorld, villagerEntity, this.constructionData);
            }
        }
        return this.currentTarget != null;
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if (this.currentTarget != null) {
            villagerEntity.getBrain().remember(MayorVillagerUtilities.BUSY, Unit.INSTANCE);
            villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
            if (villagerEntity instanceof Builder builder) {
                builder.setTaskValue(2);

                System.out.println("RUN BUILDER BREAK "+builder.getTaskValue());
            }
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = time + 100L;
        this.currentTarget = null;
        this.constructionData = null;
        villagerEntity.getBrain().forget(MayorVillagerUtilities.BUSY);

        if (villagerEntity instanceof Builder builder) {
            builder.setTaskValue(0);
            TaskHelper.updateCarryItemStack(villagerEntity);
        }
        System.out.println("FINISH BUILDER BREAK");
    }


    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {

//        System.out.println("??");

        if (this.currentTarget != null && this.constructionData != null) {
            if (this.currentTarget.getManhattanDistance(villagerEntity.getBlockPos()) <= 1 && villagerEntity instanceof Builder builder) {
                if (this.ticksRan % 20 == 0) {
                    if (!StructureHelper.getObStructiveBlockMap(serverWorld, this.constructionData).isEmpty()) {
                        boolean breakBlock = StructureHelper.breakBlock(serverWorld, this.constructionData, builder.getBuilderInventory());
                        if (!breakBlock) {
                            stop(serverWorld, villagerEntity, time);
                        }
                    } else {
                        stop(serverWorld, villagerEntity, time);
                    }
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
//        if (villagerEntity instanceof Builder builder && builder.getBuilderInventory().isEmpty()) {
//            return false;
//        }
        if (villagerEntity instanceof Builder builder && !builder.hasTargetPosition()) {
            return false;
        }
        return this.ticksRan < MAX_RUN_TIME;
    }

}

