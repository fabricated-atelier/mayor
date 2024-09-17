package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.item.StorageBlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MayorItems {
    public static final StorageBlockItem LUMBER_STORAGE_BLOCK = register("lumber_storage_block",
            new StorageBlockItem(MayorBlocks.LUMBER_STORAGE, new Item.Settings()), List.of(MayorItemGroups.MAYOR_BLOCKS));

    public static final StorageBlockItem STONE_STORAGE_BLOCK = register("stone_storage_block",
            new StorageBlockItem(MayorBlocks.STONE_STORAGE, new Item.Settings()), List.of(MayorItemGroups.MAYOR_BLOCKS));


    private static <T extends Item> T register(String name, T item, @Nullable List<MayorItemGroups.ItemGroupEntry> itemGroups) {
        Registry.register(Registries.ITEM, Mayor.identifierOf(name), item);
        if (itemGroups != null) {
            for (var entry : itemGroups) {
                entry.addItems(item);
            }
        }
        return item;
    }

    public static void initialize() {
        // static initialisation
    }
}
