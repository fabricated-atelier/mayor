package io.fabricatedatelier.mayor.network;

import io.fabricatedatelier.mayor.network.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CustomS2CNetworking {
    static {
        ClientPlayNetworking.registerGlobalReceiver(MayorViewPacket.PACKET_ID, MayorViewPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(MayorStructuresPacket.PACKET_ID, MayorStructuresPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(StructurePacket.PACKET_ID, StructurePacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(StructureOriginPacket.PACKET_ID, StructureOriginPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(VillageDataPacket.PACKET_ID, VillageDataPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(EntityListS2CPacket.PACKET_ID, EntityListS2CPacket::handlePacket);
    }

    public static void initialize() {
        // static initialisation
    }
}