package io.fabricatedatelier.mayor.entity.villager.access;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WorkerInventory extends SimpleInventory {

    public WorkerInventory(int size) {
        super(size);
    }

    public boolean isInventoryFull(@Nullable Item item) {
        for (int i = 0; i < this.size(); i++) {
            if (this.getHeldStacks().get(i).isEmpty()) {
                return false;
            } else if (item != null && this.getHeldStacks().get(i).getCount() < this.getHeldStacks().get(i).getMaxCount() && this.getHeldStacks().get(i).isOf(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean isInventoryFull(@Nullable ItemStack itemStack) {
        for (int i = 0; i < this.size(); i++) {
            if (this.getHeldStacks().get(i).isEmpty()) {
                return false;
            } else if (itemStack != null && this.getHeldStacks().get(i).getCount() < this.getHeldStacks().get(i).getMaxCount() && this.getHeldStacks().get(i).isOf(itemStack.getItem())) {
                return false;
            }
        }
        return true;
    }

    public ItemStack getFirstStack() {
        for (int i = 0; i < this.getHeldStacks().size(); i++) {
            if (!this.getHeldStacks().get(i).isEmpty()) {
                return this.getHeldStacks().get(i);
            }
        }
        return ItemStack.EMPTY;
    }

}
