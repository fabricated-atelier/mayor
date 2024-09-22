package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlock;
import io.fabricatedatelier.mayor.block.entity.VillageContainerBlockEntity;
import io.fabricatedatelier.mayor.state.VillageData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InventoryUtil {

    public static List<ItemStack> getMissingItems(List<ItemStack> availableStacksList, List<ItemStack> requiredStacksList) {
        List<ItemStack> availableStacks = new ArrayList<>();
        List<ItemStack> requiredStacks = new ArrayList<>();

        for (ItemStack stack : availableStacksList) {
            availableStacks.add(stack.copy());
        }
        for (ItemStack stack : requiredStacksList) {
            requiredStacks.add(stack.copy());
        }

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
            if (world.getBlockState(pos).getBlock() instanceof AbstractVillageContainerBlock && world.getBlockEntity(pos) instanceof VillageContainerBlockEntity villageContainerBlockEntity) {
                for (ItemStack stack : villageContainerBlockEntity.getItems()) {
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

    public static List<ItemStack> getRequiredItems(List<ItemStack> availableStacksList, List<ItemStack> missingStacksList) {

        List<ItemStack> availableStacks = availableStacksList.stream().map(ItemStack::copy).toList();
        List<ItemStack> missingStacks = missingStacksList.stream().map(ItemStack::copy).toList();

        List<ItemStack> requiredStacks = new ArrayList<>();

        for (ItemStack availableStack : availableStacks) {

            for (ItemStack missingStack : missingStacks) {
                if (missingStack.isOf(availableStack.getItem())) {
                    int availableCount = availableStack.getCount();
                    int missingCount = missingStack.getCount();

                    if (availableCount >= missingCount) {
                        ItemStack stack = adjustRequiredStack(requiredStacks, missingStack, missingCount);
                        if (!stack.isEmpty()) {
                            requiredStacks.add(stack);
                        }
                        availableStack.decrement(missingCount);
                    } else {
                        ItemStack stack = adjustRequiredStack(requiredStacks, missingStack, availableCount);
                        if (!stack.isEmpty()) {
                            requiredStacks.add(stack);
                        }
                        missingStack.decrement(availableCount);
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

    private static ItemStack adjustRequiredStack(List<ItemStack> requiredStacks, ItemStack missingStack, int count) {
        ItemStack stack = missingStack.copyWithCount(count);
        for (ItemStack requiredStack : requiredStacks) {
            if (requiredStack.isOf(missingStack.getItem())) {
                int spaceAvailable = requiredStack.getMaxCount() - requiredStack.getCount();
                int toAdd = Math.min(spaceAvailable, stack.getCount());
                requiredStack.increment(toAdd);
                stack.decrement(toAdd);
                if (stack.isEmpty()) {
                    break;
                }
            }
        }
        return stack;
    }

}
