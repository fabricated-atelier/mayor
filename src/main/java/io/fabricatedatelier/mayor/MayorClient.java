package io.fabricatedatelier.mayor;

import io.fabricatedatelier.mayor.entity.custom.client.CameraPullEntityRenderer;
import io.fabricatedatelier.mayor.init.MayorClientEvents;
import io.fabricatedatelier.mayor.init.MayorEntities;
import io.fabricatedatelier.mayor.init.MayorKeyBind;
import io.fabricatedatelier.mayor.init.MayorRenderers;
import io.fabricatedatelier.mayor.network.CustomS2CNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class MayorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CustomS2CNetworking.initialize();
        MayorKeyBind.initialize();
        MayorClientEvents.initialize();
        MayorRenderers.initialize();

        EntityRendererRegistry.register(MayorEntities.CAMERA_PULL, CameraPullEntityRenderer::new);
    }
}
