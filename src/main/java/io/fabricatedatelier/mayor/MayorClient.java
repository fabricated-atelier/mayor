package io.fabricatedatelier.mayor;

import io.fabricatedatelier.mayor.block.entity.client.LumberStorageBlockEntityRenderer;
import io.fabricatedatelier.mayor.init.BlockEntities;
import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.init.Renderer;
import io.fabricatedatelier.mayor.network.CustomS2CNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;

@Environment(EnvType.CLIENT)
public class MayorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CustomS2CNetworking.initialize();
        Renderer.initialize();
        KeyBindings.initialize();

        BlockEntityRendererFactories.register(BlockEntities.LUMBER_STORAGE, context -> new LumberStorageBlockEntityRenderer<>());
    }
}
