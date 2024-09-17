package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.entity.custom.CameraTargetEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class MayorEntities {
    public static final EntityType<CameraTargetEntity> CAMERA_TARGET = register("camera_target",
            EntityType.Builder.<CameraTargetEntity>create(CameraTargetEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f)
                    .spawnableFarFromPlayer()
                    .makeFireImmune()
                    .disableSummon()
                    .build());


    private static <E extends Entity, T extends EntityType<E>> T register(String name, T entityType) {
        return Registry.register(Registries.ENTITY_TYPE, Mayor.identifierOf(name), entityType);
    }

    public static void initialize() {
        // static initialisation
    }
}
