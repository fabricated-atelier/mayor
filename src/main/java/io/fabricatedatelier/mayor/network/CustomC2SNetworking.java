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
        ServerPlayNetworking.registerGlobalReceiver(BallotPaperPacket.PACKET_ID, BallotPaperPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(CameraPullMovementPacket.PACKET_ID, CameraPullMovementPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(DeskDataPacket.PACKET_ID, DeskDataPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(DeskScreenPacket.PACKET_ID, DeskScreenPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(DeskMayorDataPacket.PACKET_ID, DeskMayorDataPacket::handlePacket);
        ServerPlayNetworking.registerGlobalReceiver(LedgerPacket.PACKET_ID, LedgerPacket::handlePacket);
    }

    public static void initialize() {
    }
}
