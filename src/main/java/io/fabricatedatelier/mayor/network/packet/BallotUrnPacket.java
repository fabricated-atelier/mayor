package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

public record BallotUrnPacket(BlockPos blockPos, String villageName, boolean validated, long mayorPlayerTime, List<UUID> votedPlayerUuids, long voteStartTime, int voteTicks) implements CustomPayload {

    public static final CustomPayload.Id<BallotUrnPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("ballot_urn_packet"));

    public static final PacketCodec<RegistryByteBuf, BallotUrnPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBlockPos(value.blockPos);
        buf.writeString(value.villageName);
        buf.writeBoolean(value.validated);
        buf.writeLong(value.mayorPlayerTime);
        buf.writeCollection(value.votedPlayerUuids, (bufx, uuid) -> bufx.writeUuid(uuid));
        buf.writeLong(value.voteStartTime);
        buf.writeInt(value.voteTicks);
    }, buf -> new BallotUrnPacket(buf.readBlockPos(), buf.readString(), buf.readBoolean(), buf.readLong(), buf.readList(bufx -> bufx.readUuid()), buf.readLong(), buf.readInt()));

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


