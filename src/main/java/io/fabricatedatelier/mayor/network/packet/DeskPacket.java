package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public record DeskPacket(BlockPos deskPos, boolean validated, boolean mayor) implements CustomPayload {

    public static final CustomPayload.Id<DeskPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("desk_packet"));

    public static final PacketCodec<RegistryByteBuf, DeskPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBlockPos(value.deskPos);
        buf.writeBoolean(value.validated);
        buf.writeBoolean(value.mayor);
    }, buf -> new DeskPacket(buf.readBlockPos(), buf.readBoolean(), buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
    }
}


