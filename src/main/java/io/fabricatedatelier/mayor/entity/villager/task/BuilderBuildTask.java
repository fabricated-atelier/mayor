package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableMap;
import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.FarmerWorkTask;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BuilderBuildTask extends MultiTickTask<VillagerEntity> {
    //    private static final int MAX_RUN_TIME = 200;
//    public static final float WALK_SPEED = 0.5F;
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;
//    private final List<BlockPos> targetPositions = Lists.<BlockPos>newArrayList();

//    private final BlockPos targetPosition = BlockPos.ORIGIN;

    //    private MayorVillageState mayorVillageState = null;
//    private VillageData villageData = null;
    private ConstructionData constructionData = null;

    public BuilderBuildTask() {
        super(ImmutableMap.of());
    }//MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
//        FarmerWorkTask

//        System.out.println("SHOULD RUN BUILDER TASK");
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
                this.currentTarget = findClosestTarget(serverWorld, villagerEntity, builder);
            }
        }
        return this.currentTarget != null;
    }

    private List<BlockPos> getPossibleMiddleTargetBlockPoses(ServerWorld serverWorld, BlockBox blockBox) {
        List<BlockPos> blockPosList = new ArrayList<>();
        int middleX = blockBox.getMinX() + (blockBox.getMaxX() - blockBox.getMinX()) / 2;
        int middleZ = blockBox.getMinZ() + (blockBox.getMaxZ() - blockBox.getMinZ()) / 2;

        BlockPos pos;

        pos = getAirPos(serverWorld, middleX, blockBox.getMinZ(), blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }
        pos = getAirPos(serverWorld, middleX, blockBox.getMaxZ(), blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }
        pos = getAirPos(serverWorld, blockBox.getMinX(), middleZ, blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }
        pos = getAirPos(serverWorld, blockBox.getMaxX(), middleZ, blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }

        return blockPosList;
    }

    @Nullable
    private BlockPos getAirPos(ServerWorld serverWorld, int x, int z, int minY, int maxY) {
        for (int i = minY; i < maxY; i++) {
            BlockPos pos = new BlockPos(x, i, z);
            if (serverWorld.getBlockState(pos).isAir()) {
                return pos;
            }
        }
        return null;
    }


    @Nullable
    private BlockPos findClosestTarget(ServerWorld serverWorld, VillagerEntity villagerEntity, Builder builder) {
        if (this.constructionData != null) {
//            VillageData villageData = this.mayorVillageState.getVillageData(builder.getVillageCenterPosition());
//            if (villageData.getConstructions().get(builder.getTargetPosition()) != null) {
//                ConstructionData constructionData = villageData.getConstructions().get(builder.getTargetPosition());

            BlockBox blockBox = this.constructionData.getStructureData().getBlockBox();

            BlockPos targetPos = null;
            for (BlockPos pos : getPossibleMiddleTargetBlockPoses(serverWorld, blockBox)) {
                if (canReachSite(villagerEntity, pos)) {
                    if (targetPos != null) {
                        if (pos.getSquaredDistance(villagerEntity.getPos()) < targetPos.getSquaredDistance(villagerEntity.getPos())) {
                            targetPos = pos;
                        }
                    } else {
                        targetPos = pos;
                    }
                }
            }
            System.out.println("BUILD TARGET: " + targetPos + " : " + builder.getBuilderInventory().isEmpty() + " : " + builder.getBuilderInventory().getHeldStacks());
            return targetPos;
        }
        return null;
    }

    private static boolean canReachSite(PathAwareEntity entity, BlockPos pos) {
        Path path = entity.getNavigation().findPathTo(pos, 256);
        return path != null && path.reachesTarget();
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if ( this.currentTarget != null) {//time > this.nextResponseTime &&
            villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));

            System.out.println("RUN: " + this.currentTarget);
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = time + 40L;
        System.out.println("END BUILD");
    }


    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if (this.currentTarget != null) {
            if (this.currentTarget.getManhattanDistance(villagerEntity.getBlockPos()) <= 1 && villagerEntity instanceof Builder builder && !builder.getBuilderInventory().isEmpty()) {
                MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(serverWorld);
                VillageData villageData = mayorVillageState.getVillageData(builder.getVillageCenterPosition());
                if (villageData != null && villageData.getConstructions().get(builder.getTargetPosition()) != null) {

                    boolean test = StructureHelper.placeBlock(serverWorld, villageData.getConstructions().get(builder.getTargetPosition()), builder.getBuilderInventory());
System.out.println(test+ " : "+builder.getBuilderInventory().getHeldStacks());
                    // ISSUE WITH BEDS and doors
//                    System.out.print("PLACE BLOCK " + test + " : " + StructureHelper.getMissingConstructionBlockMap(serverWorld, this.constructionData));
                    if (StructureHelper.getMissingConstructionBlockMap(serverWorld, this.constructionData).isEmpty()) {
                        finishBuildTask(serverWorld, villagerEntity, time);
                    }
                }
            }
            ticksRan++;
        }
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (villagerEntity instanceof Builder builder && builder.getBuilderInventory().isEmpty()) {
            return false;
        }
        return this.ticksRan < 2000;
    }

    private void finishBuildTask(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        stop(serverWorld, villagerEntity, time);

        System.out.println("FINALLY FINISHED STRUCTURE");
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

            System.out.println("FINALLY LUL "+builder.getBuilderInventory().isEmpty()+ " : "+builder.getBuilderInventory().getHeldStacks());
            if (builder.getBuilderInventory().isEmpty()) {
                builder.setCarryItemStack(ItemStack.EMPTY);
            } else {
                // Todo: START DUMP TASK HERE
            }
            builder.setTargetPosition(null);
        }
    }
}

