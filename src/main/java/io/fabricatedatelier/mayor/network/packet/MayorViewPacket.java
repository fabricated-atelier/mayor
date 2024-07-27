package io.fabricatedatelier.mayor.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MayorViewPacket(boolean mayorView) implements CustomPayload {

    public static final CustomPayload.Id<MayorViewPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("mayor", "mayor_view_packet"));

    public static final PacketCodec<RegistryByteBuf, MayorViewPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBoolean(value.mayorView);
    }, buf -> new MayorViewPacket(buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    };
}
