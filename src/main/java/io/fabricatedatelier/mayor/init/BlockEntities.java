package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.entity.CameraDebugBlockEntity;
import io.fabricatedatelier.mayor.block.entity.LumberStorageBlockEntity;
import io.fabricatedatelier.mayor.block.entity.StoneStorageBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlockEntities {
    public static final BlockEntityType<CameraDebugBlockEntity> CAMERA_DEBUG =
            register("camera_debug", CameraDebugBlockEntity::new, Blocks.CAMERA_DEBUG);

    public static final BlockEntityType<LumberStorageBlockEntity> LUMBER_STORAGE =
            register("lumber_storage", LumberStorageBlockEntity::new, Blocks.LUMBER_STORAGE);

    public static final BlockEntityType<StoneStorageBlockEntity> STONE_STORAGE =
            register("lumber_storage", StoneStorageBlockEntity::new, Blocks.STONE_STORAGE);


    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name, BlockEntityType.BlockEntityFactory<? extends T> entityFactory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Mayor.identifierOf(name),
                BlockEntityType.Builder.<T>create(entityFactory, blocks).build());
    }

    public static void initialize() {
        // static initialisation
    }
}
