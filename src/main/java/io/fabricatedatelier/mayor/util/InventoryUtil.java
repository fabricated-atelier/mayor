package io.fabricatedatelier.mayor.util;

import net.minecraft.item.ItemStack;

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
