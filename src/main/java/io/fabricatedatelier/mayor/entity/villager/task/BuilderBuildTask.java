package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableMap;
import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.init.MayorVillagerUtilities;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.StructureHelper;
import io.fabricatedatelier.mayor.util.VillageHelper;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BuilderBuildTask extends MultiTickTask<VillagerEntity> {

    private static final int MAX_RUN_TIME = 6000;
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;

    @Nullable
    private ConstructionData constructionData = null;

    public BuilderBuildTask() {
        super(ImmutableMap.of(MayorVillagerUtilities.BUSY, MemoryModuleState.VALUE_ABSENT, MayorVillagerUtilities.SHOULD_DUMP, MemoryModuleState.VALUE_ABSENT, MayorVillagerUtilities.SHOULD_BREAK, MemoryModuleState.VALUE_ABSENT), MAX_RUN_TIME * 2 / 3, MAX_RUN_TIME);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (serverWorld.getTime() < this.nextResponseTime) {
            return false;
        }
        if (villagerEntity instanceof Builder builder && builder.getVillageCenterPosition() != null && builder.hasTargetPosition()) {
            if (builder.getBuilderInventory().isEmpty()) {
                return false;
            }
            // Todo: Set closest building position
            if (this.constructionData == null) {
                if (MayorStateHelper.getMayorVillageState(serverWorld) != null) {
                    VillageData villageData = MayorStateHelper.getMayorVillageState(serverWorld).getVillageData(builder.getVillageCenterPosition());
                    if (villageData != null) {
                        if (!villageData.getConstructions().isEmpty() && villageData.getConstructions().containsKey(builder.getTargetPosition())) {
                            this.constructionData = villageData.getConstructions().get(builder.getTargetPosition());
                        }
                    }
                }
            }
            if (this.constructionData != null) {
                this.currentTarget = VillageHelper.findClosestTarget(serverWorld, villagerEntity, this.constructionData);

//                if (!StructureHelper.getObStructiveBlockMap(serverWorld, constructionData).isEmpty()) {
//                    villagerEntity.getBrain().remember(MayorVillagerUtilities.SHOULD_BREAK, true);
//                    this.currentTarget = null;
//                }
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

//            System.out.println("RUN: " + this.currentTarget);

            System.out.println("RUN BUILDER BUILD");
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

        if (villagerEntity instanceof Builder builder ) {
            if(builder.getBuilderInventory().isEmpty()) {
                builder.setCarryItemStack(ItemStack.EMPTY);
            }else{
                System.out.println("BUILD TASK: SET SHOULD DUMP");
                villagerEntity.getBrain().remember(MayorVillagerUtilities.SHOULD_DUMP, Unit.INSTANCE);
            }
        }
        villagerEntity.getBrain().forget(MayorVillagerUtilities.BUSY);
        System.out.println("FINISH BUILDER BUILD");
    }


    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if (this.currentTarget != null && this.constructionData != null) {
            if (this.currentTarget.getManhattanDistance(villagerEntity.getBlockPos()) <= 1 && villagerEntity instanceof Builder builder && !builder.getBuilderInventory().isEmpty()) {

                if (this.ticksRan % 20 == 0) {
                    boolean placedBlock = StructureHelper.placeBlock(serverWorld, constructionData, builder.getBuilderInventory());

                    System.out.println("PLACE BLOCK " + placedBlock);

                    if (StructureHelper.getMissingConstructionBlockMap(serverWorld, this.constructionData).isEmpty()) {
                        finishBuildTask(serverWorld, villagerEntity, time);
                    } else if (!placedBlock && !builder.getBuilderInventory().isEmpty()) {
                        stop(serverWorld, villagerEntity, time);
//                        villagerEntity.getBrain().remember(MayorVillagerUtilities.SHOULD_DUMP, Unit.INSTANCE);
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

    private void finishBuildTask(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        stop(serverWorld, villagerEntity, time);

        System.out.println("FINALLY FINISHED STRUCTURE BUILD");

        this.currentTarget = null;
        if (villagerEntity instanceof Builder builder && builder.getVillageCenterPosition() != null) {
            MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(serverWorld);
            VillageData villageData = mayorVillageState.getVillageData(builder.getVillageCenterPosition());
            if (villageData != null) {
                if (builder.hasTargetPosition() && villageData.getConstructions().containsKey(builder.getTargetPosition())) {
                    villageData.addStructure(constructionData.getStructureData());
                    villageData.getConstructions().remove(builder.getTargetPosition());
                    mayorVillageState.markDirty();
                }
            }

//            System.out.println("FINALLY LUL " + builder.getBuilderInventory().isEmpty() + " : " + builder.getBuilderInventory().getHeldStacks());

            builder.setTargetPosition(null);
        }
    }
}

