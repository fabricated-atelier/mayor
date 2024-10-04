package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.util.ScreenHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public record VillageViewPacket(String villageName, int villageLevel, Optional<String> mayorName, Optional<BlockPos> votePos,
                                Integer voteTimeLeft) implements CustomPayload {

    public static final CustomPayload.Id<VillageViewPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("village_view_packet"));

    public static final PacketCodec<RegistryByteBuf, VillageViewPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeString(value.villageName);
        buf.writeInt(value.villageLevel);
        buf.writeOptional(value.mayorName, PacketByteBuf::writeString);
        buf.writeOptional(value.votePos, (bufx, pos) -> bufx.writeBlockPos(pos));
        buf.writeInt(value.voteTimeLeft);
    }, buf -> new VillageViewPacket(buf.readString(), buf.readInt(), buf.readOptional(PacketByteBuf::readString), buf.readOptional(bufx -> bufx.readBlockPos()), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        ScreenHelper.openVillageScreen(context.client(), this.villageName(), this.villageLevel, this.mayorName().orElse(""), this.votePos().orElse(null), this.voteTimeLeft());
    }

}
