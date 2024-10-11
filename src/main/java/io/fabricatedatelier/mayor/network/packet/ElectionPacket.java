package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.util.BallotUrnHelper;
import io.fabricatedatelier.mayor.util.CitizenHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record ElectionPacket(BlockPos blockPos, int voteTicks) implements CustomPayload {

    public static final CustomPayload.Id<ElectionPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("election_packet"));

    public static final PacketCodec<RegistryByteBuf, ElectionPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBlockPos(value.blockPos);
        buf.writeInt(value.voteTicks);
    }, buf -> new ElectionPacket(buf.readBlockPos(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        if (!CitizenHelper.isCitizenOfClosestVillage(context.player().getServerWorld(), context.player())) {
            return;
        }
        BallotUrnHelper.startElection(context.player().getServerWorld(), this.blockPos(), this.voteTicks());
    }
}

