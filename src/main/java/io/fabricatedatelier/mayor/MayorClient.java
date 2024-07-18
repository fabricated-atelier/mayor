package io.fabricatedatelier.mayor;

import io.fabricatedatelier.mayor.network.CustomS2CNetworking;
import net.fabricmc.api.ClientModInitializer;

public class MayorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CustomS2CNetworking.initialize();
    }
}
