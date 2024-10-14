package io.fabricatedatelier.mayor.network;

import io.fabricatedatelier.mayor.network.packet.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class CustomS2CNetworking {
    static {
        ClientPlayNetworking.registerGlobalReceiver(MayorViewPacket.PACKET_ID, MayorViewPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(MayorStructuresPacket.PACKET_ID, MayorStructuresPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(VillageDataPacket.PACKET_ID, VillageDataPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(EntityListS2CPacket.PACKET_ID, EntityListS2CPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(BallotUrnPacket.PACKET_ID, BallotUrnPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(BallotPaperScreenPacket.PACKET_ID, BallotPaperScreenPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(VillageViewPacket.PACKET_ID, VillageViewPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(ExtraVillageInfoPacket.PACKET_ID, ExtraVillageInfoPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(DeskCitizenScreenPacket.PACKET_ID, DeskCitizenScreenPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(DeskMayorScreenPacket.PACKET_ID, DeskMayorScreenPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(DeskPacket.PACKET_ID, DeskPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(DeskMayorDataPacket.PACKET_ID, DeskMayorDataPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(LedgerPacket.PACKET_ID, LedgerPacket::handlePacket);
    }

    public static void initialize() {
        // static initialisation
    }
}