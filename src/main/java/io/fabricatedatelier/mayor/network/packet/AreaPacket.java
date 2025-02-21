package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.entity.villager.access.Worker;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.StateHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record AreaPacket(int entityId, BlockPos targetPos) implements CustomPayload {

    public static final CustomPayload.Id<AreaPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("area_packet"));


    public static final PacketCodec<RegistryByteBuf, AreaPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.entityId);
        buf.writeBlockPos(value.targetPos);
    }, buf -> new AreaPacket(buf.readInt(), buf.readBlockPos()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        VillageData villageData = StateHelper.getClosestVillage(context.player().getServerWorld(), context.player().getBlockPos());
        if (villageData != null && (context.player().isCreativeLevelTwoOp() || villageData.getMayorPlayerUuid() != null && villageData.getMayorPlayerUuid().equals(context.player().getUuid()))) {
            if (context.player().getServerWorld().getEntityById(this.entityId()) instanceof Worker worker) {
                worker.setTargetPosition(this.targetPos());
            }
        }
    }
}

