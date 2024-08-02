package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.custom.CameraDebugBlock;
import io.fabricatedatelier.mayor.block.custom.LumberStorageBlock;
import io.fabricatedatelier.mayor.block.custom.StoneStorageBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.List;

public class Blocks {
    public static final CameraDebugBlock CAMERA_DEBUG = register("camera_debug", new CameraDebugBlock(AbstractBlock.Settings.create()), true);
    public static final LumberStorageBlock LUMBER_STORAGE = register("lumber_storage", new LumberStorageBlock(AbstractBlock.Settings.create()), false);
    public static final StoneStorageBlock STONE_STORAGE = register("stone_storage", new StoneStorageBlock(AbstractBlock.Settings.create()), false);


    private static <T extends Block> T register(String name, T block, boolean hasDefaultItem) {
        Registry.register(Registries.BLOCK, Mayor.identifierOf(name), block);
        if (hasDefaultItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, Mayor.identifierOf(name), blockItem);
            ItemGroups.MAYOR_BLOCKS.addItems(blockItem);
        }
        return block;
    }

    public static void initialize() {
        // static initialisation
    }
}
