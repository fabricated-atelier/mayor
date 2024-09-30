package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.block.entity.client.LumberStorageBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class MayorRenderers {

    public static void initialize() {
        BlockEntityRendererFactories.register(MayorBlockEntities.VILLAGE_STORAGE, context -> new LumberStorageBlockEntityRenderer<>());
    }
}
