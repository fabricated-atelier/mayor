package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record StructureCenterPacket(boolean center) implements CustomPayload {

    public static final CustomPayload.Id<StructureCenterPacket> PACKET_ID = new CustomPayload.Id<>(Mayor.identifierOf("structure_center_packet"));

    public static final PacketCodec<RegistryByteBuf, StructureCenterPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOL, StructureCenterPacket::center, StructureCenterPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
            mayorManager.setStructureCentered(center);
            StructureHelper.updateMayorStructure(context.player(), mayorManager.getStructureId(), mayorManager.getStructureRotation(), this.center);
        });
    }
}
