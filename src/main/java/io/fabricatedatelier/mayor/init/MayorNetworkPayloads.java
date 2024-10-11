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
        registerC2S(EntityListC2SPacket.PACKET_ID, EntityListC2SPacket.PACKET_CODEC);
        registerC2S(EntityViewPacket.PACKET_ID, EntityViewPacket.PACKET_CODEC);
        registerC2S(StructureBuildPacket.PACKET_ID, StructureBuildPacket.PACKET_CODEC);
        registerC2S(MayorUpdatePacket.PACKET_ID, MayorUpdatePacket.PACKET_CODEC);
        registerC2S(ElectionPacket.PACKET_ID, ElectionPacket.PACKET_CODEC);
        registerC2S(BallotPaperPacket.PACKET_ID, BallotPaperPacket.PACKET_CODEC);
        registerC2S(CameraPullMovementPacket.PACKET_ID, CameraPullMovementPacket.PACKET_CODEC);

        // S2C
        registerS2C(MayorViewPacket.PACKET_ID, MayorViewPacket.PACKET_CODEC);
        registerS2C(VillageDataPacket.PACKET_ID, VillageDataPacket.PACKET_CODEC);
        registerS2C(MayorStructuresPacket.PACKET_ID, MayorStructuresPacket.PACKET_CODEC);
        registerS2C(EntityListS2CPacket.PACKET_ID, EntityListS2CPacket.PACKET_CODEC);
        registerS2C(BallotUrnPacket.PACKET_ID, BallotUrnPacket.PACKET_CODEC);
        registerS2C(BallotPaperScreenPacket.PACKET_ID, BallotPaperScreenPacket.PACKET_CODEC);
        registerS2C(VillageViewPacket.PACKET_ID, VillageViewPacket.PACKET_CODEC);
        registerS2C(ExtraVillageInfoPacket.PACKET_ID, ExtraVillageInfoPacket.PACKET_CODEC);
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
