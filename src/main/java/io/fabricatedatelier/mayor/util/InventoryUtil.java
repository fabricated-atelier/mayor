package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlock;
import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlockEntity;
import io.fabricatedatelier.mayor.state.VillageData;
import net.minecraft.item.ItemStack;
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

        for (int i = 0; i < villageData.getStorageOriginBlockPosList().size(); i++) {
            if (world.getBlockState(villageData.getStorageOriginBlockPosList().get(i)).getBlock() instanceof AbstractVillageContainerBlock && world.getBlockEntity(villageData.getStorageOriginBlockPosList().get(i)) instanceof AbstractVillageContainerBlockEntity abstractVillageContainerBlockEntity) {
                availableStacks.addAll(abstractVillageContainerBlockEntity.getItems());
                // Todo: Instead of addAll, increase count if smaller than maxCount

            //                    for (int u = 0; u < this.availableStacks.size(); u++) {
            //                        if (this.availableStacks.get(u).isOf() && this.availableStacks.get(u).getCount() < this.availableStacks.get(u).getMaxCount()) {
            //
            //                        }
            //                    }
            //                    this.availableStacks.add();
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
