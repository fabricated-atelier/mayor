package io.fabricatedatelier.mayor.datagen;

import io.fabricatedatelier.mayor.init.Blocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.util.Identifier;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(Blocks.CAMERA_DEBUG,
                        Identifier.ofVanilla("block/dirt"))
        );

        /*blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.LUMBER_STORAGE)
                .coordinate(createMultiBlockStructureMap()));*/
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }

    /*private BlockStateVariantMap createMultiBlockStructureMap() {
        return BlockStateVariantMap.create(MayorProperties.POSITION)
                .register(position -> BlockStateVariant.create().put(VariantSettings.MODEL, Mayor.identifierOf("block/1x1/lumber_1x1_top")));
    }*/
}
