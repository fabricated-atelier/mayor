package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableMap;
import io.fabricatedatelier.mayor.block.entity.VillageContainerBlockEntity;
import io.fabricatedatelier.mayor.entity.villager.access.Worker;
import io.fabricatedatelier.mayor.init.MayorVillagerUtilities;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.VillageState;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.InventoryUtil;
import io.fabricatedatelier.mayor.util.StateHelper;
import io.fabricatedatelier.mayor.util.StructureHelper;
import io.fabricatedatelier.mayor.util.TaskHelper;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BuilderCollectTask extends MultiTickTask<VillagerEntity> {

    private static final int MAX_RUN_TIME = 6000;
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;

    public BuilderCollectTask() {
        super(ImmutableMap.of(MayorVillagerUtilities.BUSY, MemoryModuleState.VALUE_ABSENT), MAX_RUN_TIME * 2 / 3, MAX_RUN_TIME);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (serverWorld.getTime() < this.nextResponseTime) {
            return false;
        }
        if (villagerEntity instanceof Worker worker) {
            if (worker.getVillageCenterPosition() != null && worker.hasTargetPosition() && worker.getWorkerInventory().isEmpty()) {
                VillageState villageState = StateHelper.getMayorVillageState(serverWorld);
                if (villageState.getVillageData(worker.getVillageCenterPosition()) != null) {
                    VillageData villageData = villageState.getVillageData(worker.getVillageCenterPosition());
                    if (villageData.getConstructions().get(worker.getTargetPosition()) != null) {
                        ConstructionData constructionData = villageData.getConstructions().get(worker.getTargetPosition());

                        if (!StructureHelper.getObStructiveBlockMap(serverWorld, constructionData).isEmpty()) {
                            this.currentTarget = null;
                            this.nextResponseTime = serverWorld.getTime() + 100L;
                        } else {
                            this.currentTarget = getTarget(serverWorld, villagerEntity, villageData, constructionData);
                        }
//                        System.out.println("BUILDER COLLECT TASK: " + villagerEntity + " : " + this.currentTarget);


//                            System.out.println("FROM COLLECT SET SHOULD BREAK: " + StructureHelper.getObStructiveBlockMap(serverWorld, constructionData));
//                            villagerEntity.getBrain().remember(MayorVillagerUtilities.SHOULD_BREAK, Unit.INSTANCE);

//                        }
                    }
                }

                return this.currentTarget != null;
            }
        }
        return false;
    }


    @Nullable
    private BlockPos getTarget(ServerWorld serverWorld, VillagerEntity villagerEntity, VillageData villageData, ConstructionData constructionData) {
        if (villageData.getStorageOriginBlockPosList().size() <= 0) {
            return null;
        }
        if (!StructureHelper.getMissingConstructionBlockMap(serverWorld, constructionData).isEmpty()) {

            Item item = StructureHelper.getMissingConstructionBlockMap(serverWorld, constructionData).values().stream().findFirst().get().getBlock().asItem();
            for (int i = 0; i < villageData.getStorageOriginBlockPosList().size(); i++) {
                if (serverWorld.getBlockEntity(villageData.getStorageOriginBlockPosList().get(i)) instanceof VillageContainerBlockEntity villageContainerBlockEntity) {
                    if (villageContainerBlockEntity.contains(item)) {
                        if (villageContainerBlockEntity.getStructureOriginPos().isPresent() && TaskHelper.canReachSite(villagerEntity, villageContainerBlockEntity.getStructureOriginPos().get())) {
                            return villageContainerBlockEntity.getStructureOriginPos().get();
                        } else {
                            for (BlockPos pos : villageContainerBlockEntity.getConnectedBlocks()) {
                                if (TaskHelper.canReachSite(villagerEntity, pos)) {
                                    return pos;
                                }
                            }
                        }
                        return null;
                    }

                    // Todo: Maybe get closest storage blockpos of the of the storage multiblock
//                        break;
                }
            }
        }

//        for (Map.Entry<BlockPos, BlockState> entry : constructionData.getBlockMap().entrySet()) {
//            if (!serverWorld.getBlockState(entry.getKey()).isOf(entry.getValue().getBlock())) {
////                entry.getValue().getBlock();
//                // Todo: Find the correct Lumber,Stone or whatever storage block
//                for (int i = 0; i < villageData.getStorageOriginBlockPosList().size(); i++) {
//                    if (serverWorld.getBlockEntity(villageData.getStorageOriginBlockPosList().get(i)) instanceof VillageContainerBlockEntity abstractVillageContainerBlockEntity) {
//                        // Find the correct one lul
//                        return villageData.getStorageOriginBlockPosList().get(i);
////                        break;
//                    }
//                }
//                break;
//            }
//        }
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
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if (this.currentTarget != null) {
            villagerEntity.getBrain().remember(MayorVillagerUtilities.BUSY, Unit.INSTANCE);
            villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.7F, 1));

            System.out.println("RUN BUILDER COLLECT");
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = time + 40L;
        this.currentTarget = null;

        villagerEntity.getBrain().forget(MayorVillagerUtilities.BUSY);

        TaskHelper.updateCarryItemStack(villagerEntity);
        System.out.println("FINISH BUILDER COLLECT");
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
//        System.out.println("KEEP " + villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET));


        if (this.currentTarget != null) {

            if (this.currentTarget.getManhattanDistance(villagerEntity.getBlockPos()) <= 1) {
                if (serverWorld.getBlockEntity(this.currentTarget) instanceof VillageContainerBlockEntity containerBlockEntity && containerBlockEntity.getStructureOriginBlockEntity().isPresent() && villagerEntity instanceof Worker worker) {
//                    villageContainerBlockEntity.getItems();
//                    villageContainerBlockEntity.getori
//                    if(builder.getBuilderInventory().){
//
//                    }
                    VillageData villageData = StateHelper.getMayorVillageState(serverWorld).getVillageData(worker.getVillageCenterPosition());
                    if (villageData != null) {

                        List<ItemStack> missingItemStacks = StructureHelper.getMissingConstructionItemStacks(serverWorld, villageData.getConstructions().get(worker.getTargetPosition()));
                        if (!missingItemStacks.isEmpty()) {
                            VillageContainerBlockEntity villageContainerBlockEntity = containerBlockEntity.getStructureOriginBlockEntity().get();
//                            List<ItemStack> availableItemStacks = containerBlockEntity.getStructureOriginBlockEntity().get().getItems();
//                            for (ItemStack stack : missingItemStacks) {
//                            }
//                            for (ItemStack stack : containerBlockEntity.getStructureOriginBlockEntity().get().getItems()) {
//                                ItemStack copyStack = stack.copy();
//                                for (ItemStack missingStack : missingItemStacks) {
//                                    if (missingStack.isOf(copyStack.getItem())) {
//                                        if (builder.getBuilderInventory().isInventoryFull(copyStack)) {
//                                            return;
//                                        } else {
//                                            missingStack.decrement(copyStack.getCount());
//                                            copyStack = builder.getBuilderInventory().addStack(copyStack);
//                                            if (copyStack.isEmpty()) {
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
////                                if(stac &&!builder.getBuilderInventory().isInventoryFull(stack)){
////      break;
////                                }
//                            }
                            System.out.println("REAL MISSING?: " + missingItemStacks);
                            System.out.println("CONTAINER: " + villageContainerBlockEntity.getItems());
//                            builder.getBuilderInventory().
                            List<ItemStack> requiredStacks = InventoryUtil.getRequiredItems(villageContainerBlockEntity.getItems(), missingItemStacks);


                            System.out.println("REQUIRED STACKS: " + requiredStacks);
                            System.out.println("COLLECTED INVENTORY: " + worker.getWorkerInventory());

                            for (ItemStack requiredStack : requiredStacks) {
                                if (!worker.getWorkerInventory().isInventoryFull(requiredStack)) {
                                    worker.getWorkerInventory().addStack(requiredStack.copy());
                                    villageContainerBlockEntity.removeStack(requiredStack);

                                    System.out.println("REMOVE STACK FROM VILLAGE CONTAINER BLOCK: " + requiredStack);
                                } else {
                                    System.out.println("AFTER FILLING VIL INV: " + villageContainerBlockEntity.getItems());
                                    break;
                                }
                            }

                            System.out.println("SET COLLECT STACKS: " + worker.getWorkerInventory());
                            villageContainerBlockEntity.markDirty();
                            serverWorld.updateListeners(villageContainerBlockEntity.getPos(), villageContainerBlockEntity.getCachedState(), villageContainerBlockEntity.getCachedState(), 0);
//                            System.out.println("FILL INVENTORY :D " + builder.getBuilderInventory().getHeldStacks());

                        } else {
                            stop(serverWorld, villagerEntity, serverWorld.getTime());
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
        if (villagerEntity instanceof Worker worker && (!worker.getWorkerInventory().isEmpty() || !worker.hasTargetPosition())) {
            return false;
        }
        return this.ticksRan < MAX_RUN_TIME;
    }
}


