package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.custom.CameraDebugBlock;
import io.fabricatedatelier.mayor.block.custom.LumberStorageBlock;
import io.fabricatedatelier.mayor.block.custom.StoneStorageBlock;
import io.fabricatedatelier.mayor.item.StoneStorageBlockItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Blocks {
    public static final Block CAMERA_DEBUG = registerWithItem("camera_debug", new CameraDebugBlock(AbstractBlock.Settings.create()));
    public static final Block LUMBER_STORAGE = registerWithItem("lumber_storage", new LumberStorageBlock(AbstractBlock.Settings.create()));
    public static final Block STONE_STORAGE = register("stone_storage",
            new StoneStorageBlock(AbstractBlock.Settings.create()),
            new BlockItemData<>(
                    new StoneStorageBlockItem(Blocks.STONE_STORAGE, new Item.Settings()),
                    List.of(ItemGroups.MAYOR_ITEMS)
            )
    );


    private static <T extends Block, U extends BlockItem> T register(String name, T block, @Nullable BlockItemData<U> blockItemData) {
        Registry.register(Registries.BLOCK, Mayor.identifierOf(name), block);
        if (blockItemData != null) {
            Registry.register(Registries.ITEM, Mayor.identifierOf(name), blockItemData.blockItem);
            if (blockItemData.itemGroups() != null) {
                Items.addToItemGroups(blockItemData.blockItem, blockItemData.itemGroups());
            }
        }
        return block;
    }

    private static <T extends Block> T registerWithItem(String name, T block) {
        return register(name, block, new BlockItemData<>(new BlockItem(block, new Item.Settings()), List.of(ItemGroups.MAYOR_BLOCKS)));
    }

    private record BlockItemData<T extends BlockItem>(T blockItem, @Nullable List<RegistryKey<ItemGroup>> itemGroups) {
    }

    public static void initialize() {
        // static initialisation
    }
}
