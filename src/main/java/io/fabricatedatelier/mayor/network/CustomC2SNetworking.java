package io.fabricatedatelier.mayor.network;

import io.fabricatedatelier.mayor.network.packet.OriginBlockPosPacket;
import io.fabricatedatelier.mayor.network.packet.StructurePacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CustomC2SNetworking {
    static {
        // ServerPlayNetworking.registerGlobalReceiver(CustomPacket.IDENTIFIER, CustomPacket::handlePacket);
        // ServerPlayNetworking.registerGlobalReceiver(CustomPacket.IDENTIFIER, CustomPacket::handlePacket);
    }

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(StructurePacket.PACKET_ID, StructurePacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(OriginBlockPosPacket.PACKET_ID, OriginBlockPosPacket.PACKET_CODEC);
        // PayloadTypeRegistry.playC2S().register(OriginBlockPosPacket.PACKET_ID, OriginBlockPosPacket.PACKET_CODEC);

        // static initialisation
    }
}
