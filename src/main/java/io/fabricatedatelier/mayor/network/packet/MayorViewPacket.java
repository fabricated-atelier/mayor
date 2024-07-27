package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.util.MayorManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record MayorViewPacket(boolean mayorView) implements CustomPayload {

    public static final CustomPayload.Id<MayorViewPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("mayor_view_packet"));

    public static final PacketCodec<RegistryByteBuf, MayorViewPacket> PACKET_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.BOOL, MayorViewPacket::mayorView,
                    MayorViewPacket::new
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
        mayorManager.setMajorView(this.mayorView);
    }
}
