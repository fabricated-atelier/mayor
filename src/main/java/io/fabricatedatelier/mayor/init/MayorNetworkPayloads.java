package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.network.packet.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class MayorNetworkPayloads {
    static {
        // C2S
        registerC2S(MayorViewPacket.PACKET_ID, MayorViewPacket.PACKET_CODEC);
        registerC2S(StructureRotatePacket.PACKET_ID, StructureRotatePacket.PACKET_CODEC);
        registerC2S(StructureCenterPacket.PACKET_ID, StructureCenterPacket.PACKET_CODEC);
        registerC2S(EntityListC2SPacket.PACKET_ID, EntityListC2SPacket.PACKET_CODEC);
        registerC2S(EntityViewPacket.PACKET_ID, EntityViewPacket.PACKET_CODEC);
        registerC2S(StructureBuildPacket.PACKET_ID, StructureBuildPacket.PACKET_CODEC);
        registerC2S(MayorUpdatePacket.PACKET_ID, MayorUpdatePacket.PACKET_CODEC);

        // S2C
        registerS2C(MayorViewPacket.PACKET_ID, MayorViewPacket.PACKET_CODEC);
        registerS2C(VillageDataPacket.PACKET_ID, VillageDataPacket.PACKET_CODEC);
        registerS2C(MayorStructuresPacket.PACKET_ID, MayorStructuresPacket.PACKET_CODEC);
        registerS2C(StructurePacket.PACKET_ID, StructurePacket.PACKET_CODEC);
        registerS2C(StructureOriginPacket.PACKET_ID, StructureOriginPacket.PACKET_CODEC);
        registerS2C(EntityListS2CPacket.PACKET_ID, EntityListS2CPacket.PACKET_CODEC);
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
