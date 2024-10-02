package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.item.screen.BallotPaperScreen;
import io.fabricatedatelier.mayor.util.BallotUrnHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// Unused votedUuid field
public record BallotPaperS2CPacket(Optional<UUID> votedUuid, Optional<String> votedName, Map<UUID, String> availablePlayers) implements CustomPayload {

    public static final CustomPayload.Id<BallotPaperS2CPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("ballot_paper_s2c_packet"));

    public static final PacketCodec<RegistryByteBuf, BallotPaperS2CPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeOptional(value.votedUuid, (bufx, uuid) -> bufx.writeUuid(uuid));
        buf.writeOptional(value.votedName, PacketByteBuf::writeString);
        buf.writeMap(value.availablePlayers, (bufx, uuid) -> bufx.writeUuid(uuid), PacketByteBuf::writeString);
    }, buf -> new BallotPaperS2CPacket(buf.readOptional(bufx -> bufx.readUuid()), buf.readOptional(PacketByteBuf::readString), buf.readMap(bufx -> bufx.readUuid(), PacketByteBuf::readString)));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }


    public void handlePacket(ClientPlayNetworking.Context context) {
        BallotUrnHelper.openBallotPaperScreen(context.client(), this.votedName().orElse(""), this.availablePlayers());
    }
}


