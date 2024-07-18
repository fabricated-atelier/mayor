package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
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
    public static final Item TEST_ITEM = register("test", new Item(new Item.Settings()), List.of(ItemGroups.MAYOR_ITEMS));


    private static <T extends Item> T register(String name, T item, @Nullable List<RegistryKey<ItemGroup>> itemGroups) {
        Registry.register(Registries.ITEM, Mayor.identifierOf(name), item);
        if (itemGroups != null) addToItemGroups(item, itemGroups);
        return item;
    }

    public static void addToItemGroups(Item item, List<RegistryKey<ItemGroup>> itemGroups) {
        for (var group : itemGroups) {
            ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(new ItemStack(item)));
        }
    }

    public static void initialize() {
        // static initialisation
    }
}
