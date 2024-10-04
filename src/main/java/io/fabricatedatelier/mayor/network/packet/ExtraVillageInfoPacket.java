package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record ExtraVillageInfoPacket(int availableBuilderCount) implements CustomPayload {

    public static final CustomPayload.Id<ExtraVillageInfoPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("extra_village_info_packet"));

    public static final PacketCodec<RegistryByteBuf, ExtraVillageInfoPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.availableBuilderCount);
    }, buf -> new ExtraVillageInfoPacket(buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    // May use this packet as a sync packet if mayor is in mayor view but
    // vill dies, iron golem dies, new structure, (maybe future citizen count update?)
    // If so, use optional and sync only specific values
    public void handlePacket(ClientPlayNetworking.Context context) {
        ((MayorManagerAccess) context.player()).getMayorManager().setAvailableBuilder(this.availableBuilderCount());
    }
}


