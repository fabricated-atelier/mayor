package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlock;
import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlockEntity;
import io.fabricatedatelier.mayor.state.VillageData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InventoryUtil {

    public static List<ItemStack> getMissingItems(List<ItemStack> availableStacksList, List<ItemStack> requiredStacksList) {
        List<ItemStack> availableStacks = new ArrayList<>(availableStacksList);
        List<ItemStack> requiredStacks = new ArrayList<>(requiredStacksList);

        for (ItemStack availableStack : availableStacks) {
            Iterator<ItemStack> requiredIterator = requiredStacks.iterator();

            while (requiredIterator.hasNext()) {
                ItemStack requiredStack = requiredIterator.next();

                if (requiredStack.isOf(availableStack.getItem())) {
                    int availableCount = availableStack.getCount();
                    int requiredCount = requiredStack.getCount();

                    if (availableCount >= requiredCount) {
                        availableStack.setCount(availableCount - requiredCount);
                        requiredIterator.remove();
                    } else {
                        requiredStack.decrement(availableCount);
                        availableStack.setCount(0);
                    }

                    if (availableStack.getCount() == 0) {
                        break;
                    }
                }
            }
        }

        return requiredStacks;
    }

    public static List<ItemStack> getAvailableItems(VillageData villageData, World world) {
        List<ItemStack> availableStacks = new ArrayList<>();

        for (BlockPos pos : villageData.getStorageOriginBlockPosList()) {
            if (world.getBlockState(pos).getBlock() instanceof AbstractVillageContainerBlock && world.getBlockEntity(pos) instanceof AbstractVillageContainerBlockEntity abstractVillageContainerBlockEntity) {
                for (ItemStack stack : abstractVillageContainerBlockEntity.getItems()) {
                    stack = stack.copy();
                    if (stack.isEmpty()) {
                        continue;
                    }
                    if (availableStacks.isEmpty()) {
                        availableStacks.add(stack);
                    } else {
                        for (int u = 0; u < availableStacks.size(); u++) {
                            if (ItemStack.areItemsAndComponentsEqual(availableStacks.get(u), stack)
                                    && availableStacks.get(u).isStackable()
                                    && availableStacks.get(u).getCount() < availableStacks.get(u).getMaxCount()) {
                                if ((availableStacks.get(u).getCount() + stack.getCount()) < availableStacks.get(u).getMaxCount()) {
                                    availableStacks.get(u).setCount(availableStacks.get(u).getCount() + stack.getCount());
                                    break;
                                } else {
                                    stack.setCount(stack.getCount() - (availableStacks.get(u).getMaxCount() - availableStacks.get(u).getCount()));
                                    availableStacks.get(u).setCount(availableStacks.get(u).getMaxCount());
                                }
                            } else if (u == availableStacks.size() - 1) {
                                availableStacks.add(stack);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return availableStacks;
    }

    // Todo: make MayorScreen more performant to create following method
//    public static boolean hasMissingItems(List<ItemStack> availableStacksList, List<ItemStack> requiredStacksList){
//        List<ItemStack> availableStacks = new ArrayList<>(availableStacksList);
//        List<ItemStack> requiredStacks = new ArrayList<>(requiredStacksList);
//
//        for (ItemStack availableStack : availableStacks) {
//            Iterator<ItemStack> requiredIterator = requiredStacks.iterator();
//
//            while (requiredIterator.hasNext()) {
//                ItemStack requiredStack = requiredIterator.next();
//
//                if (requiredStack.isOf(availableStack.getItem())) {
//                    int availableCount = availableStack.getCount();
//                    int requiredCount = requiredStack.getCount();
//
//                    if (availableCount >= requiredCount) {
//                        availableStack.setCount(availableCount - requiredCount);
//                        requiredIterator.remove();
//                    } else {
//                        requiredStack.decrement(availableCount);
//                        availableStack.setCount(0);
//                    }
//
//                    if (availableStack.getCount() == 0) {
//                        break;
//                    }
//                }
//            }
//        }
//        return false;
//    }

}
