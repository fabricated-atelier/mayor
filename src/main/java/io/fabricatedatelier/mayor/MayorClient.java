package io.fabricatedatelier.mayor;

import io.fabricatedatelier.mayor.block.entity.client.LumberStorageBlockEntityRenderer;
import io.fabricatedatelier.mayor.init.MayorClientEvents;
import io.fabricatedatelier.mayor.init.MayorBlockEntities;
import io.fabricatedatelier.mayor.init.MayorKeyBindings;
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
        MayorKeyBindings.initialize();
        MayorClientEvents.initialize();

        BlockEntityRendererFactories.register(MayorBlockEntities.VILLAGE_STORAGE, context -> new LumberStorageBlockEntityRenderer<>());
    }
}
