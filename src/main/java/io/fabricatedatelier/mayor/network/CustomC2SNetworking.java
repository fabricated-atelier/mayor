package io.fabricatedatelier.mayor.network;

import io.fabricatedatelier.mayor.network.packet.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CustomC2SNetworking {
    static {
        ServerPlayNetworking.registerGlobalReceiver(MayorViewPacket.PACKET_ID, MayorViewPacket::handleClientPacket);
        ServerPlayNetworking.registerGlobalReceiver(EntityListC2SPacket.PACKET_ID, EntityListC2SPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(EntityViewPacket.PACKET_ID, EntityViewPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(StructureBuildPacket.PACKET_ID, StructureBuildPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(MayorUpdatePacket.PACKET_ID, MayorUpdatePacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(ElectionPacket.PACKET_ID, ElectionPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(BallotPaperC2SPacket.PACKET_ID, BallotPaperC2SPacket::handlePacket);
    }

    public static void initialize() {
    }
}
