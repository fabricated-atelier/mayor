package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record EntityListC2SPacket(List<UUID> entityList) implements CustomPayload {

    public static final CustomPayload.Id<EntityListC2SPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("entity_list_c2s_packet"));


    public static final PacketCodec<RegistryByteBuf, EntityListC2SPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeCollection(value.entityList, (bufx, uuid) -> bufx.writeUuid(uuid));
    }, buf -> new EntityListC2SPacket(buf.readList(bufx -> bufx.readUuid())));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }


    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        List<Integer> entityIds = new ArrayList<>();
        for (int i = 0; i < this.entityList().size(); i++) {
            if (context.player().getServerWorld().getEntity(this.entityList().get(i)) instanceof LivingEntity livingEntity) {
                entityIds.add(livingEntity.getId());
            }
        }
        new EntityListS2CPacket(entityIds).sendPacket(context.player());
    }
}

