package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.custom.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class MayorBlocks {
    public static final CameraDebugBlock CAMERA_DEBUG = register("camera_debug", new CameraDebugBlock(AbstractBlock.Settings.create()), true);
    public static final LumberStorageBlock LUMBER_STORAGE = register("lumber_storage", new LumberStorageBlock(AbstractBlock.Settings.create()), false);
    public static final StoneStorageBlock STONE_STORAGE = register("stone_storage", new StoneStorageBlock(AbstractBlock.Settings.create()), false);

    public static final Block CONSTRUCTION_TABLE = register("construction_table", new Block(AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE_SLAB)), false);
    public static final Block DESK = register("desk", new DeskBlock(AbstractBlock.Settings.copy(Blocks.LECTERN)), false);
    @Deprecated
    public static final Block POLE = register("pole", new PoleBlock(AbstractBlock.Settings.copy(Blocks.STONE_SLAB)), false);

    public static final Block WOODCUTTER = register("woodcutter", new WoodCutterBlock(AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS)), false);
    public static final Block MINER_TABLE = register("miner_table", new Block(AbstractBlock.Settings.copy(Blocks.STONE_SLAB)), false);

    /**
     * If you need a custom {@link BlockItem}, register it in the {@link MayorItems} class with the corresponding Block entry.
     * @param hasDefaultItem set to false, if you want no item or are registering a custom BlockItem for it.
     */
    private static <T extends Block> T register(String name, T block, boolean hasDefaultItem) {
        Registry.register(Registries.BLOCK, Mayor.identifierOf(name), block);
        if (hasDefaultItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, Mayor.identifierOf(name), blockItem);
            MayorItemGroups.MAYOR_BLOCKS.addItems(blockItem);
        }
        return block;
    }

    public static void initialize() {
        // static initialisation
    }
}
