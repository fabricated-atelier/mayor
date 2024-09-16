package io.fabricatedatelier.mayor.datagen.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.structure.pool.StructurePool;

import java.util.concurrent.CompletableFuture;

public abstract class StructurePoolTagProvider extends FabricTagProvider<StructurePool> {
    public StructurePoolTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, RegistryKeys.TEMPLATE_POOL, completableFuture);
    }
}
