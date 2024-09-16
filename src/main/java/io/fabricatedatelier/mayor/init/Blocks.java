package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.custom.CameraDebugBlock;
import io.fabricatedatelier.mayor.block.custom.LumberStorageBlock;
import io.fabricatedatelier.mayor.block.custom.StoneStorageBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Blocks {
    public static final CameraDebugBlock CAMERA_DEBUG = register("camera_debug",
            new CameraDebugBlock(AbstractBlock.Settings.create()), true);

    public static final LumberStorageBlock LUMBER_STORAGE = register("lumber_storage",
            new LumberStorageBlock(AbstractBlock.Settings.create().strength(1.5f, 2.0f)), false);

    public static final StoneStorageBlock STONE_STORAGE = register("stone_storage",
            new StoneStorageBlock(AbstractBlock.Settings.create().strength(1.5f, 2.0f)), false);


    /**
     * If you need a custom {@link BlockItem}, register it in the {@link Items} class with the corresponding Block entry.
     * @param hasDefaultItem set to false, if you want no item or if you are registering a custom BlockItem for it.
     */
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
