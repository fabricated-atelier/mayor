package io.fabricatedatelier.mayor;

import io.fabricatedatelier.mayor.block.entity.client.LumberStorageBlockEntityRenderer;
import io.fabricatedatelier.mayor.init.ClientEvents;
import io.fabricatedatelier.mayor.init.BlockEntities;
import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.network.CustomS2CNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class MayorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CustomS2CNetworking.initialize();
        KeyBindings.initialize();
        ClientEvents.initialize();

        BlockEntityRendererFactories.register(BlockEntities.VILLAGE_STORAGE, context -> new LumberStorageBlockEntityRenderer<>());
    }
}
