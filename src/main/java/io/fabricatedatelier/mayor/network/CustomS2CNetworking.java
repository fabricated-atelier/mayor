package io.fabricatedatelier.mayor.network;

import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.network.packet.StructureOriginPacket;
import io.fabricatedatelier.mayor.network.packet.StructurePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CustomS2CNetworking {
    static {
        ClientPlayNetworking.registerGlobalReceiver(MayorViewPacket.PACKET_ID, MayorViewPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(StructurePacket.PACKET_ID, StructurePacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(StructureOriginPacket.PACKET_ID, StructureOriginPacket::handlePacket);
    }

    public static void initialize() {
        // static initialisation
    }
}