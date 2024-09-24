package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableMap;
import io.fabricatedatelier.mayor.block.entity.VillageContainerBlockEntity;
import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.StructureHelper;
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
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BuilderDumpTask extends MultiTickTask<VillagerEntity> {
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;

    public BuilderDumpTask() {
        //   super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
        // super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
        super(ImmutableMap.of(MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT), 200);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (villagerEntity instanceof Builder builder) {
            if (builder.getVillageCenterPosition() != null && !builder.getBuilderInventory().isEmpty()) {
                MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(serverWorld);
                if (mayorVillageState.getVillageData(builder.getVillageCenterPosition()) != null) {
                    VillageData villageData = mayorVillageState.getVillageData(builder.getVillageCenterPosition());
                    if (builder.hasTargetPosition()) {
                        if (villageData.getConstructions().get(builder.getTargetPosition()) != null) {
                            ConstructionData constructionData = villageData.getConstructions().get(builder.getTargetPosition());
                            if (StructureHelper.getMissingConstructionBlockMap(serverWorld, constructionData).isEmpty()) {
                                this.currentTarget = getTarget(serverWorld, builder, villageData);
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
                    if (villageContainerBlockEntity.getStructureOriginPos().isPresent() && canReachSite(builder.getVillagerEntity(), villageContainerBlockEntity.getStructureOriginPos().get())) {
                        return villageContainerBlockEntity.getStructureOriginPos().get();
                    } else {
                        for (BlockPos pos : villageContainerBlockEntity.getConnectedBlocks()) {
                            if (canReachSite(builder.getVillagerEntity(), pos)) {
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

    private static boolean canReachSite(PathAwareEntity entity, BlockPos pos) {
        Path path = entity.getNavigation().findPathTo(pos, 256);
        return path != null && path.reachesTarget();
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (l > this.nextResponseTime && this.currentTarget != null) {
            villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.7F, 1));
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.SECONDARY_JOB_SITE);
        this.ticksRan = 0;
        this.nextResponseTime = l + 40L;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (this.currentTarget != null) {
            if (this.currentTarget.getManhattanDistance(villagerEntity.getBlockPos()) <= 1) {
                if (serverWorld.getBlockEntity(this.currentTarget) instanceof VillageContainerBlockEntity containerBlockEntity && containerBlockEntity.getStructureOriginBlockEntity().isPresent() && villagerEntity instanceof Builder builder) {
                    VillageData villageData = MayorStateHelper.getMayorVillageState(serverWorld).getVillageData(builder.getVillageCenterPosition());
                    if (villageData != null) {

//                        List<ItemStack> missingItemStacks = StructureHelper.getMissingConstructionItemStacks(serverWorld, villageData.getConstructions().get(builder.getTargetPosition()));
//                        if (!missingItemStacks.isEmpty()) {
                        VillageContainerBlockEntity villageContainerBlockEntity = containerBlockEntity.getStructureOriginBlockEntity().get();
//                        if(villageContainerBlockEntity.is){
//
//                        }
//                            List<ItemStack> requiredStacks = InventoryUtil.getRequiredItems(villageContainerBlockEntity.getItems(), missingItemStacks);
//                            for (ItemStack requiredStack : requiredStacks) {
//                                if (!builder.getBuilderInventory().isInventoryFull(requiredStack)) {
//                                    builder.getBuilderInventory().addStack(requiredStack);
//                                    villageContainerBlockEntity.removeStack(requiredStack);
//                                } else {
//                                    break;
//                                }
//                            }

                        villageContainerBlockEntity.markDirty();
                        serverWorld.updateListeners(villageContainerBlockEntity.getPos(), villageContainerBlockEntity.getCachedState(), villageContainerBlockEntity.getCachedState(), 0);
//                            System.out.println("FILL INVENTORY :D " + builder.getBuilderInventory().getHeldStacks());
//
//                        } else {
//                            stop(serverWorld, villagerEntity, serverWorld.getTime());
//                        }

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
        return this.ticksRan < 2000;
    }
}


