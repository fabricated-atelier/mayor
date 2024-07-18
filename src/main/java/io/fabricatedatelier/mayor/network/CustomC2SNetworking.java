package io.fabricatedatelier.mayor.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CustomC2SNetworking {
    static {
        // ServerPlayNetworking.registerGlobalReceiver(CustomPacket.IDENTIFIER, CustomPacket::handlePacket);
        // ServerPlayNetworking.registerGlobalReceiver(CustomPacket.IDENTIFIER, CustomPacket::handlePacket);
    }

    public static void initialize() {
        // static initialisation
    }
}
