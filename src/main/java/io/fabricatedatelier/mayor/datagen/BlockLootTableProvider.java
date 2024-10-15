package io.fabricatedatelier.mayor.datagen;

import io.fabricatedatelier.mayor.init.MayorBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {

    public BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(MayorBlocks.LUMBER_STORAGE, MayorBlocks.LUMBER_STORAGE);
        addDrop(MayorBlocks.STONE_STORAGE, MayorBlocks.STONE_STORAGE);
        addDrop(MayorBlocks.CONSTRUCTION_TABLE, MayorBlocks.CONSTRUCTION_TABLE);
        addDrop(MayorBlocks.DESK, MayorBlocks.DESK);
        addDrop(MayorBlocks.POLE, MayorBlocks.POLE);
    }
}
