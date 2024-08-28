package io.fabricatedatelier.mayor.network;

import io.fabricatedatelier.mayor.network.packet.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CustomC2SNetworking {
    static {
        ServerPlayNetworking.registerGlobalReceiver(StructureRotatePacket.PACKET_ID, StructureRotatePacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(StructureCenterPacket.PACKET_ID, StructureCenterPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(MayorViewPacket.PACKET_ID, MayorViewPacket::handleClientPacket);
        ServerPlayNetworking.registerGlobalReceiver(EntityListC2SPacket.PACKET_ID, EntityListC2SPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(EntityViewPacket.PACKET_ID, EntityViewPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(StructureBuildPacket.PACKET_ID, StructureBuildPacket::handlePacket);
    }

    public static void initialize() {
    }
}
