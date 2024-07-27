package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.network.packet.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class NetworkPayloads {
    static {
        // C2S
        registerC2S(StructureRotatePacket.PACKET_ID, StructureRotatePacket.PACKET_CODEC);
        registerC2S(StructureCenterPacket.PACKET_ID, StructureCenterPacket.PACKET_CODEC);

        // S2C
        registerS2C(StructurePacket.PACKET_ID, StructurePacket.PACKET_CODEC);
        registerS2C(StructureOriginPacket.PACKET_ID, StructureOriginPacket.PACKET_CODEC);
        registerS2C(MayorViewPacket.PACKET_ID, MayorViewPacket.PACKET_CODEC);
    }

    private static <T extends CustomPayload> void registerS2C(CustomPayload.Id<T> packetIdentifier, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(packetIdentifier, codec);
    }

    private static <T extends CustomPayload> void registerC2S(CustomPayload.Id<T> packetIdentifier, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(packetIdentifier, codec);
    }

    public static void initialize() {
        // static initialisation
    }
}
