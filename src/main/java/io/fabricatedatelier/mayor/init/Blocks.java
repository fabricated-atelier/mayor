package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.CameraDebugBlock;
import io.fabricatedatelier.mayor.block.custom.LumberStorageBlock;
import io.fabricatedatelier.mayor.block.custom.StoneStorageBlock;
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
    public static final Block STONE_STORAGE = registerWithItem("stone_storage", new StoneStorageBlock(AbstractBlock.Settings.create()));


    private static <T extends Block, U extends BlockItem> T register(String name, T block, @Nullable BlockItemData blockItemData) {
        Registry.register(Registries.BLOCK, Mayor.identifierOf(name), block);
        if (blockItemData != null) {
            BlockItem blockItem = new BlockItem(block, blockItemData.itemSettings());
            Registry.register(Registries.ITEM, Mayor.identifierOf(name), blockItem);
            if (blockItemData.itemGroups() != null) {
                Items.addToItemGroups(blockItem, blockItemData.itemGroups());
            }
        }
        return block;
    }

    private static <T extends Block> T registerWithItem(String name, T block) {
        return register(name, block, new BlockItemData(new Item.Settings(), List.of(ItemGroups.MAYOR_ITEMS)));
    }

    private record BlockItemData(Item.Settings itemSettings, @Nullable List<RegistryKey<ItemGroup>> itemGroups) {
    }

    public static void initialize() {
        // static initialisation
    }
}
