package io.fabricatedatelier.mayor.datagen;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.MayorProperties;
import io.fabricatedatelier.mayor.init.MayorBlocks;
import io.fabricatedatelier.mayor.init.MayorItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(MayorBlocks.CAMERA_DEBUG,
                        Identifier.ofVanilla("block/dirt"))
        );

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(MayorBlocks.LUMBER_STORAGE)
                .coordinate(createMultiBlockStructureMap("lumber")));

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(MayorBlocks.STONE_STORAGE)
                .coordinate(createMultiBlockStructureMap("lumber")));   //TODO: use different model?
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(MayorItems.LUMBER_STORAGE_BLOCK,
                new Model(Optional.of(Mayor.identifierOf("block/lumber_single")), Optional.empty()));
        itemModelGenerator.register(MayorItems.STONE_STORAGE_BLOCK,   //TODO: use different model?
                new Model(Optional.of(Mayor.identifierOf("block/lumber_single")), Optional.empty()));

        itemModelGenerator.register(MayorBlocks.CONSTRUCTION_TABLE.asItem(),
                new Model(Optional.of(Mayor.identifierOf("block/construction_table")), Optional.empty()));

        itemModelGenerator.register(MayorItems.DECONSTRUCTION_HAMMER, Models.HANDHELD);

        itemModelGenerator.register(MayorItems.BALLOT_POTTERY_SHERD, Models.GENERATED);

        itemModelGenerator.register(MayorItems.BALLOT_PAPER, Models.GENERATED);
    }

    private BlockStateVariantMap createMultiBlockStructureMap(String name) {
        return BlockStateVariantMap.create(MayorProperties.POSITION).register(position ->
                BlockStateVariant.create().put(
                        VariantSettings.MODEL,
                        Mayor.identifierOf("block/%s_%s".formatted(name, position.asString()))
                )
        );
    }
}
