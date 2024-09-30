package io.fabricatedatelier.mayor;

import io.fabricatedatelier.mayor.init.MayorClientEvents;
import io.fabricatedatelier.mayor.init.MayorKeyBindings;
import io.fabricatedatelier.mayor.init.MayorRenderers;
import io.fabricatedatelier.mayor.network.CustomS2CNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MayorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CustomS2CNetworking.initialize();
        MayorKeyBindings.initialize();
        MayorClientEvents.initialize();
        MayorRenderers.initialize();
    }
}
