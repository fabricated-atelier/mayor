package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.entity.custom.CameraPullEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class MayorEntities {
    public static final EntityType<CameraPullEntity> CAMERA_PULL = register("camera_pull",
            EntityType.Builder.<CameraPullEntity>create(CameraPullEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f)
                    .spawnableFarFromPlayer()
                    .makeFireImmune()
                    // .disableSummon()
                    .build());


    private static <E extends Entity, T extends EntityType<E>> T register(String name, T entityType) {
        return Registry.register(Registries.ENTITY_TYPE, Mayor.identifierOf(name), entityType);
    }

    public static void initialize() {
        // static initialisation
    }
}
