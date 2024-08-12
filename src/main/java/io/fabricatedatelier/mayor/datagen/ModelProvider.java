package io.fabricatedatelier.mayor.datagen;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.init.Blocks;
import io.fabricatedatelier.mayor.block.Properties;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
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

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(Blocks.LUMBER_STORAGE)
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
                .coordinate(createMultiBlockStructureMap()));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }

    private BlockStateVariantMap createMultiBlockStructureMap() {
        return BlockStateVariantMap.create(Properties.SHAPE, Properties.POSITION, Properties.SIDE)
                .register((part, position, side) -> switch (part) {
                            case ALL_WALLS -> BlockStateVariant.create().put(VariantSettings.MODEL,
                                    Mayor.identifierOf("block/1x1/lumber_1x1"));
                            case TWO_WALLS_END -> {
                                String model = "block/1xn/lumber_1xn_end_";
                                if (position.equals(Properties.VerticalPosition.BOTTOM)) model += "bottom";
                                else model += "top";

                                yield BlockStateVariant.create().put(VariantSettings.MODEL,
                                        Mayor.identifierOf(model));
                            }
                            case TWO_WALLS_MID -> {
                                String model = "block/1xn/lumber_1xn_mid_";

                                if (position.equals(Properties.VerticalPosition.BOTTOM)) model += "bottom";
                                else model += "top";

                                yield BlockStateVariant.create().put(VariantSettings.MODEL,
                                        Mayor.identifierOf(model));
                            }
                            case ONE_WALL_END -> {
                                String model = "block/2xn/lumber_2xn_end";

                                if (side.equals(Properties.Side.RIGHT)) model += "_right";
                                else model += "_left";

                                if (position.equals(Properties.VerticalPosition.TOP)) model += "_top";

                                yield BlockStateVariant.create().put(VariantSettings.MODEL,
                                        Mayor.identifierOf(model));
                            }
                            case ONE_WALL_MID -> {
                                String model = "block/2xn/lumber_2xn_mid";

                                if (side.equals(Properties.Side.RIGHT)) model += "_right";
                                else model += "_left";

                                if (position.equals(Properties.VerticalPosition.TOP)) model += "_top";

                                yield BlockStateVariant.create().put(VariantSettings.MODEL,
                                        Mayor.identifierOf(model));
                            }
                        }
                );
    }
}
