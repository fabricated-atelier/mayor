package io.fabricatedatelier.mayor.network.packet;

import java.util.Optional;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record StructureOriginPacket(Optional<BlockPos> origin) implements CustomPayload {

    public static final CustomPayload.Id<StructureOriginPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("mayor", "structure_origin_packet"));

    public static final PacketCodec<RegistryByteBuf, StructureOriginPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeOptional(value.origin, (bufx, pos) -> bufx.writeBlockPos(pos));
    }, buf -> new StructureOriginPacket(buf.readOptional(bufx -> bufx.readBlockPos())));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    };
}
