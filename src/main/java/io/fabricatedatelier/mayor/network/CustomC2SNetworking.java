package io.fabricatedatelier.mayor.network;

import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.network.packet.StructureCenterPacket;
import io.fabricatedatelier.mayor.network.packet.StructureRotatePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CustomC2SNetworking {
    static {
        ServerPlayNetworking.registerGlobalReceiver(StructureRotatePacket.PACKET_ID, StructureRotatePacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(StructureCenterPacket.PACKET_ID, StructureCenterPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(MayorViewPacket.PACKET_ID, MayorViewPacket::handleClientPacket);
    }

    public static void initialize() {
    }
}
