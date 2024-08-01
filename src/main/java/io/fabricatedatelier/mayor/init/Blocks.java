package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.custom.CameraDebugBlock;
import io.fabricatedatelier.mayor.block.custom.LumberStorageBlock;
import io.fabricatedatelier.mayor.block.custom.StoneStorageBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Blocks {
    public static final CameraDebugBlock CAMERA_DEBUG = register("camera_debug", new CameraDebugBlock(AbstractBlock.Settings.create()));
    public static final LumberStorageBlock LUMBER_STORAGE = register("lumber_storage", new LumberStorageBlock(AbstractBlock.Settings.create()));
    public static final StoneStorageBlock STONE_STORAGE = register("stone_storage", new StoneStorageBlock(AbstractBlock.Settings.create()));


    private static <T extends Block> T register(String name, T block) {
        Registry.register(Registries.BLOCK, Mayor.identifierOf(name), block);
        return block;
    }

    public static void initialize() {
        // static initialisation
    }
}
