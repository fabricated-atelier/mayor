package io.fabricatedatelier.mayor.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CustomS2CNetworking {
    static {
        // ClientPlayNetworking.registerGlobalReceiver(CustomPacket.IDENTIFIER, CustomPacket::handlePacket);
        // ClientPlayNetworking.registerGlobalReceiver(CustomPacket.IDENTIFIER, CustomPacket::handlePacket);
    }

    public static void initialize() {
        // static initialisation
    }
}