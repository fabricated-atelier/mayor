package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.entity.CameraDebugBlockEntity;
import io.fabricatedatelier.mayor.block.entity.VillageContainerBlockEntity;
import io.fabricatedatelier.mayor.util.HandledInventory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class MayorBlockEntities {
    public static final BlockEntityType<CameraDebugBlockEntity> CAMERA_DEBUG =
            register("camera_debug", CameraDebugBlockEntity::new, MayorBlocks.CAMERA_DEBUG);

    public static final BlockEntityType<VillageContainerBlockEntity> VILLAGE_STORAGE =
            registerWithStorage("lumber_storage", VillageContainerBlockEntity::new,
                    MayorBlocks.LUMBER_STORAGE, MayorBlocks.STONE_STORAGE);


    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name, BlockEntityType.BlockEntityFactory<? extends T> entityFactory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Mayor.identifierOf(name),
                BlockEntityType.Builder.<T>create(entityFactory, blocks).build(null));
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends BlockEntity> BlockEntityType<T> registerWithStorage(
            String name, BlockEntityType.BlockEntityFactory<? extends T> entityFactory, Block... blocks) {
        BlockEntityType<T> blockEntityType = register(name, entityFactory, blocks);
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            if (blockEntity instanceof HandledInventory inventory) return inventory.getAsStorage(direction);
            Mayor.LOGGER.error("BlockEntity was missing HandledInventory Interface at registration");
            return null;
        }, blockEntityType);
        return blockEntityType;
    }


    public static void initialize() {
        // static initialisation
    }
}
