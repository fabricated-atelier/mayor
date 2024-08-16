package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.ServerPlayerAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record EntityViewPacket(int entityId) implements CustomPayload {

    public static final CustomPayload.Id<EntityViewPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("entity_view_packet"));


    public static final PacketCodec<RegistryByteBuf, EntityViewPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, EntityViewPacket::entityId, EntityViewPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }


    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        if (context.player().getServerWorld().getEntityById(entityId) instanceof LivingEntity livingEntity) {
            ((ServerPlayerAccess) context.player()).setWasInMayorView(true);
            context.player().setCameraEntity(livingEntity);
        }
    }
}

