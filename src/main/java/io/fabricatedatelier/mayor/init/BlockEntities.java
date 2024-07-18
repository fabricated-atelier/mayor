package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlockEntities {


    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name, BlockEntityType.BlockEntityFactory<? extends T> entityFactory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Mayor.identifierOf(name),
                BlockEntityType.Builder.<T>create(entityFactory, blocks).build());
    }

    public static void initialize() {
        // static initialisation
    }
}
