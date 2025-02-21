package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.datagen.TagProvider;
import io.fabricatedatelier.mayor.entity.villager.access.Worker;
import io.fabricatedatelier.mayor.state.ConstructionData;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TaskHelper {


    public static boolean canReachSite(PathAwareEntity entity, BlockPos pos) {
        Path path = entity.getNavigation().findPathTo(pos, 2, 256);
        return path != null && path.reachesTarget();
    }

    @Nullable
    public static BlockPos getAirPos(ServerWorld serverWorld, int x, int z, int minY, int maxY) {
        for (int i = minY; i < maxY; i++) {
            BlockPos pos = new BlockPos(x, i, z);
            if (serverWorld.getBlockState(pos).isAir()) {
                return pos;
            }
        }
        return null;
    }

    public static List<BlockPos> getPossibleMiddleTargetBlockPoses(ServerWorld serverWorld, BlockBox blockBox) {
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
    public static BlockPos findClosestTarget(ServerWorld serverWorld, VillagerEntity villagerEntity, ConstructionData constructionData) {
        if (constructionData != null) {
            BlockBox blockBox = constructionData.getStructureData().getBlockBox();

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
            return targetPos;
        }
        return null;
    }

    public static void updateCarryItemStack(VillagerEntity villagerEntity) {
        if (villagerEntity instanceof Worker worker) {
            if (worker.getWorkerInventory().isEmpty()) {
                worker.setCarryItemStack(ItemStack.EMPTY);
            } else {
                for (ItemStack stack : worker.getWorkerInventory().getHeldStacks()) {
                    if (!stack.isEmpty() && stack.getItem() instanceof BlockItem && stack.isIn(TagProvider.ItemTags.CARRIABLE)) {
                        worker.setCarryItemStack(stack);
                        break;
                    }
                }
            }
        }
    }

    public static void pickUpItems(ServerWorld serverWorld, VillagerEntity villagerEntity, TagKey<Item>... tags) {
        if (villagerEntity instanceof Worker worker) {
            for (ItemEntity itemEntity : serverWorld
                    .getNonSpectatingEntities(ItemEntity.class, villagerEntity.getBoundingBox().expand(1.5D, 0D, 1.5D))) {
                if (!itemEntity.isRemoved() && !itemEntity.getStack().isEmpty() && !itemEntity.cannotPickup()) {
                    boolean canPickUp = false;
                    for (TagKey<Item> tag : tags) {
                        if (itemEntity.getStack().isIn(tag)) {
                            canPickUp = true;
                            break;
                        }
                    }
                    if (canPickUp) {
                        ItemStack itemStack = itemEntity.getStack();
                        if (worker.getWorkerInventory().canInsert(itemEntity.getStack())) {
                            villagerEntity.triggerItemPickedUpByEntityCriteria(itemEntity);
                            int i = itemStack.getCount();
                            ItemStack itemStack2 = worker.getWorkerInventory().addStack(itemStack);
                            villagerEntity.sendPickup(itemEntity, i - itemStack2.getCount());
                            if (itemStack2.isEmpty()) {
                                itemEntity.discard();
                            } else {
                                itemStack.setCount(itemStack2.getCount());
                            }
                        }
                    }
                }
            }
        }
    }

}
