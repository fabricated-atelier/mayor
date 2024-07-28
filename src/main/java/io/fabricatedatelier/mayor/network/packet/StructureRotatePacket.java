package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.util.MayorManager;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record StructureRotatePacket(boolean rotateLeft) implements CustomPayload {

    public static final CustomPayload.Id<StructureRotatePacket> PACKET_ID = new CustomPayload.Id<>(Mayor.identifierOf("structure_rotate_packet"));

    public static final PacketCodec<RegistryByteBuf, StructureRotatePacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOL, StructureRotatePacket::rotateLeft, StructureRotatePacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(new StructureRotatePacket(rotateLeft));
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
        StructureHelper.updateMayorStructure(context.player(), mayorManager.getStructureId(), StructureHelper.getRotatedStructureRotation(mayorManager.getStructureRotation(), this.rotateLeft()),
                mayorManager.getStructureCentered());
    }
}
