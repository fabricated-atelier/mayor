package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.item.LumberStorageBlockItem;
import io.fabricatedatelier.mayor.item.StoneStorageBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Items {
    public static final LumberStorageBlockItem LUMBER_STORAGE_BLOCK = register("lumber_storage_block",
            new LumberStorageBlockItem(Blocks.LUMBER_STORAGE, new Item.Settings()), List.of(ItemGroups.MAYOR_BLOCKS));

    public static final StoneStorageBlockItem STONE_STORAGE_BLOCK = register("stone_storage_block",
            new StoneStorageBlockItem(Blocks.STONE_STORAGE, new Item.Settings()), List.of(ItemGroups.MAYOR_BLOCKS));


    private static <T extends Item> T register(String name, T item, @Nullable List<RegistryKey<ItemGroup>> itemGroups) {
        Registry.register(Registries.ITEM, Mayor.identifierOf(name), item);
        if (itemGroups != null) addToItemGroups(item, itemGroups);
        return item;
    }

    public static void addToItemGroups(Item item, List<RegistryKey<ItemGroup>> itemGroups) {
        for (var group : itemGroups) {
            ItemGroupEvents.modifyEntriesEvent(group).register(entries -> {
                entries.add(new ItemStack(item));
            });
        }
    }

    public static void initialize() {
        // static initialisation
    }
}
