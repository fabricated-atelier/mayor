package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.entity.villager.access.Worker;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

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
        List<BlockPos> targetPositions = new ArrayList<>();
        for (int i = 0; i < this.entityList().size(); i++) {
            if (context.player().getServerWorld().getEntity(this.entityList().get(i)) instanceof VillagerEntity villagerEntity && villagerEntity instanceof Worker worker) {
                entityIds.add(villagerEntity.getId());
                targetPositions.add(worker.getTargetPosition());
            }
        }
        new EntityListS2CPacket(entityIds, targetPositions).sendPacket(context.player());
    }
}

