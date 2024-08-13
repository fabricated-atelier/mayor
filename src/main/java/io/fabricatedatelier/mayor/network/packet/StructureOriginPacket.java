package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.manager.MayorManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public record StructureOriginPacket(Optional<BlockPos> origin) implements CustomPayload {

    public static final CustomPayload.Id<StructureOriginPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("structure_origin_packet"));

    public static final PacketCodec<RegistryByteBuf, StructureOriginPacket> PACKET_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.optional(BlockPos.PACKET_CODEC), StructureOriginPacket::origin,
                    StructureOriginPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }


    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
        if (mayorManager.getStructureOriginBlockPos() != null) {
            mayorManager.setStructureOriginBlockPos(null);
        } else {
            mayorManager.setStructureOriginBlockPos(this.origin.orElse(null));
        }
    }
}
