package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.init.MayorComponents;
import io.fabricatedatelier.mayor.init.MayorItems;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record BallotPaperPacket(UUID votedUuid) implements CustomPayload {

    public static final CustomPayload.Id<BallotPaperPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("ballot_paper_packet"));

    public static final PacketCodec<RegistryByteBuf, BallotPaperPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeUuid(value.votedUuid);
    }, buf -> new BallotPaperPacket(buf.readUuid()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        if (context.player().getMainHandStack().isOf(MayorItems.BALLOT_PAPER)) {
            context.player().getMainHandStack().set(MayorComponents.VOTE_UUID, this.votedUuid());
        }
    }
}

