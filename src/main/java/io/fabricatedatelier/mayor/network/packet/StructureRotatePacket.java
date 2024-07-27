package io.fabricatedatelier.mayor.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StructureRotatePacket(boolean rotateLeft) implements CustomPayload {

    public static final CustomPayload.Id<StructureRotatePacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("mayor", "structure_rotate_packet"));

    public static final PacketCodec<RegistryByteBuf, StructureRotatePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBoolean(value.rotateLeft);
    }, buf -> new StructureRotatePacket(buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    };
}
