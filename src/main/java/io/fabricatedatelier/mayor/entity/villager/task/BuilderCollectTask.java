package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableMap;
import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.entity.VillageContainerBlockEntity;
import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BuilderCollectTask extends MultiTickTask<VillagerEntity> {
    //    private static final int MAX_RUN_TIME = 200;
    //    public static final float WALK_SPEED = 0.5F;
    @Nullable
    private BlockPos currentTarget;
    private long nextResponseTime;
    private int ticksRan;
//    private final List<BlockPos> targetPositions = Lists.<BlockPos>newArrayList();

//    private final BlockPos targetPosition = BlockPos.ORIGIN;

    public BuilderCollectTask() {
     //   super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
       // super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
        super(ImmutableMap.of(),200);
    }

    @Override
    protected boolean hasRequiredMemoryState(VillagerEntity entity) {
        boolean test =
         super.hasRequiredMemoryState(entity);
//        System.out.println("LOL TEST "+test);
//        Mayor.LOGGER.warn("LOL");
        return test;
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

//        System.out.println("???");

        if (villagerEntity instanceof Builder builder) {
            if (builder.getVillageCenterPosition() != null && builder.hasTargetPosition() && builder.getBuilderInventory().isEmpty()) {

                // Todo: Set the needed target Position here
                // this is the needed position of the abstract village container block with the correct items
                MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(serverWorld);
                if (mayorVillageState.getVillageData(builder.getVillageCenterPosition()) != null) {
                    VillageData villageData = mayorVillageState.getVillageData(builder.getVillageCenterPosition());
                    if (villageData.getConstructions().get(builder.getTargetPosition()) != null) {
                        ConstructionData constructionData = villageData.getConstructions().get(builder.getTargetPosition());
//                        constructionData.getStructureData().
//                        constructionData.getBlockMap();

//                        villageData.getStorageOriginBlockPosList();

                        this.currentTarget = getTarget(serverWorld, villageData, constructionData);
                        System.out.println("BUILDER COLLECT TASK: "+villagerEntity+ " : "+this.currentTarget);
                    }
                }
                // Todo: Find current target
//                return builder.getBuilderInventory().isEmpty();


//                mayorVillageState.

                return this.currentTarget != null;
            }
        }
        return false;
    }

    @Nullable
    private BlockPos getTarget(ServerWorld serverWorld, VillageData villageData, ConstructionData constructionData) {
        if (villageData.getStorageOriginBlockPosList().size() <= 0) {
            return null;
        }
        if (!StructureHelper.getMissingConstructionBlockMap(serverWorld, constructionData).isEmpty()) {

            Item item = StructureHelper.getMissingConstructionBlockMap(serverWorld, constructionData).values().stream().findFirst().get().getBlock().asItem();
            for (int i = 0; i < villageData.getStorageOriginBlockPosList().size(); i++) {
                if (serverWorld.getBlockEntity(villageData.getStorageOriginBlockPosList().get(i)) instanceof VillageContainerBlockEntity villageContainerBlockEntity) {
                    if(villageContainerBlockEntity.contains(item)){
                        return villageData.getStorageOriginBlockPosList().get(i);
                    }
                    // Find the correct one lul
                    // Todo: villageContainerBlockEntity contains item

                    // Todo: Maybe get closest storage blockpos of the of the storage multiblock
//                        break;
//                    CowEntity
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
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        System.out.println("RUN");
        if (l > this.nextResponseTime && this.currentTarget != null) {
            System.out.println("SET TARGETS");
            villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.7F, 1));

            //SECONDARY_JOB_SITE
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = l + 40L;
        System.out.println("FINISH");
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
//        System.out.println("KEEP");
        if (this.currentTarget == null || this.currentTarget.isWithinDistance(villagerEntity.getPos(), 1.0)) {
            if (this.currentTarget != null && l > this.nextResponseTime) {
                BlockState blockState = serverWorld.getBlockState(this.currentTarget);
//                Block block = blockState.getBlock();
//                Block block2 = serverWorld.getBlockState(this.currentTarget.down()).getBlock();

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
                if (villagerEntity instanceof Builder builder && !builder.getBuilderInventory().isEmpty()) {
                    stop(serverWorld, villagerEntity, l);
                    System.out.println("STOP");
                }
            }

            this.ticksRan++;
        }
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (villagerEntity instanceof Builder builder && !builder.getBuilderInventory().isEmpty()) {
            System.out.println("OOOOOOOOOOO "+builder.getBuilderInventory());
            return false;
        }
        // Todo: Tweak should keep RUnning until the vill has stored some things in its inventory
//        System.out.println("SHOULD KEEP RUNNING "+this.ticksRan);
        return this.ticksRan < 2000;
    }
}


