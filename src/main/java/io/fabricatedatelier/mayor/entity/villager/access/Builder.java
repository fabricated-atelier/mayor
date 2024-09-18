package io.fabricatedatelier.mayor.entity.villager.access;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface Builder {

    VillagerEntity getVillagerEntity();

    SimpleInventory getBuilderInventory();

//    static void pickUpItem(MobEntity entity, BuilderInventory builderInventory, ItemEntity item) {
//        ItemStack itemStack = item.getStack();
//        if (entity.canGather(itemStack)) {
//            SimpleInventory simpleInventory = builderInventory.getBuilderInventory();
//            boolean bl = simpleInventory.canInsert(itemStack);
//            if (!bl) {
//                return;
//            }
//
//            entity.triggerItemPickedUpByEntityCriteria(item);
//            int i = itemStack.getCount();
//            ItemStack itemStack2 = simpleInventory.addStack(itemStack);
//            entity.sendPickup(item, i - itemStack2.getCount());
//            if (itemStack2.isEmpty()) {
//                item.discard();
//            } else {
//                itemStack.setCount(itemStack2.getCount());
//            }
//        }
//    }


    @Nullable
    BlockPos getVillageCenterPosition();

    void setVillageCenterPosition(@Nullable BlockPos villageCenterPosition);

    boolean hasTargetPosition();

    BlockPos getTargetPosition();

    void setTargetPosition(@Nullable BlockPos targetPosition);

    default void readBuilderInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (nbt.contains("BuilderInventory", NbtElement.LIST_TYPE)) {
            this.getBuilderInventory().readNbtList(nbt.getList("BuilderInventory", NbtElement.COMPOUND_TYPE), wrapperLookup);
        }
    }

    default void writeBuilderInventory(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbt.put("BuilderInventory", this.getBuilderInventory().toNbtList(wrapperLookup));
    }
}

