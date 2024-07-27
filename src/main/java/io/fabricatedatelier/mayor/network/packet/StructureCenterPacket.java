package io.fabricatedatelier.mayor.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StructureCenterPacket(boolean center) implements CustomPayload {

    public static final CustomPayload.Id<StructureCenterPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("mayor", "structure_center_packet"));

    public static final PacketCodec<RegistryByteBuf, StructureCenterPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBoolean(value.center);
    }, buf -> new StructureCenterPacket(buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    };
}
