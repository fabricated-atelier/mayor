package io.fabricatedatelier.mayor;

import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.init.Renderer;
import io.fabricatedatelier.mayor.network.CustomS2CNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MayorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CustomS2CNetworking.initialize();
        Renderer.initialize();
        KeyBindings.initialize();
    }
}
